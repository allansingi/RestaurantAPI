package pt.allanborges.restaurant.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import pt.allanborges.restaurant.controller.handlers.exceptions.ResourceNotFoundException;
import pt.allanborges.restaurant.model.dtos.DishDTO;
import pt.allanborges.restaurant.model.dtos.DishFilterDTO;
import pt.allanborges.restaurant.model.entities.Dish;
import pt.allanborges.restaurant.model.enums.DishCode;
import pt.allanborges.restaurant.model.mapper.DishMapper;
import pt.allanborges.restaurant.repository.DishRepository;
import pt.allanborges.restaurant.service.DishService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Log4j2
@AllArgsConstructor
@Service
public class DishServiceImpl implements DishService {

    private static final String PRICE = "price";
    private static final String STOCK = "stock";

    private final DishRepository dishRepository;
    private final DishMapper dishMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DishDTO createDish(final DishDTO dishDTO) {
        return dishMapper.toDTO(dishRepository.save(dishMapper.toEntity(dishDTO)));
    }

    @Override
    public List<DishDTO> findAllDishes() {
        return dishMapper.toDTOList(dishRepository.findByInactivatedDateIsNull());
    }

    @Override
    public DishDTO getDishById(final Long id) {
        return dishMapper.toDTO(dishRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Dish not found. Id: " + id)));
    }

    @Override
    @Transactional
    public DishDTO updateDish(final Long dishId, final DishDTO dishDTO) {
        Dish current = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found. Id: " + dishId));
        dishMapper.updateEntityFromDTO(dishDTO, current);
        Dish saved = dishRepository.save(current);
        return dishMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public Page<DishDTO> findAllDishesPaginatedWithFilters(final Integer page,
                                                           final Integer size,
                                                           final String sort,
                                                           final String orderBy,
                                                           final DishFilterDTO filter) {
        log.info("Returning paginated DishDTO list with filters");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sort), orderBy));
        Page<DishDTO> response = findAllDishesWithFilters(filter, pageable).map(dishMapper::toDTO);
        List<DishDTO> updatedContent = response.getContent();
        return new PageImpl<>(updatedContent, pageable, response.getTotalElements());
    }

    @Transactional
    public Page<Dish> findAllDishesWithFilters(DishFilterDTO filter, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Dish> query = builder.createQuery(Dish.class);
        Root<Dish> requestRoot = query.from(Dish.class);
        List<Predicate> predicates = buildPredicates(filter, builder, requestRoot);
        query.where(predicates.toArray(new Predicate[0]));

        if (pageable.getSort().isSorted())
            query.orderBy(getOrderList(pageable, builder, requestRoot));

        List<Dish> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = getTotalCount(filter, builder);
        return new PageImpl<>(resultList, pageable, total);
    }

    private List<Predicate> buildPredicates(DishFilterDTO filter, CriteriaBuilder builder, Root<Dish> root) {
        List<Predicate> predicates = new ArrayList<>();
        addEqualPredicateForLong(filter.getId(), root.get("id"), builder, predicates);
        addLikePredicate(filter.getName(), root.get("name"), builder, predicates);
        addLikePredicate(filter.getDescription(), root.get("description"), builder, predicates);
        addEqualPredicateForBigDecimal(filter.getPrice(), root.get(PRICE), builder, predicates);
        addEqualPredicateForInteger(filter.getStock(), root.get(STOCK), builder, predicates);
        addInPredicateForEnumCodes(filter.getCode(), root.get("code"), predicates);
        addDateRangePredicate(filter.getCreatedDateFrom(), filter.getCreatedDateTo(), builder, root.get("createdDate"), predicates);
        return predicates;
    }

    private void addInPredicateForEnumCodes(List<String> codes,
                                            Path<DishCode> path,
                                            List<Predicate> predicates) {
        if (codes == null || codes.isEmpty())
            return;

        List<DishCode> wanted = codes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .flatMap(s -> Arrays.stream(s.split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .map(s -> {
                    try {
                        return DishCode.valueOf(s);
                    }
                    catch (IllegalArgumentException ex) {
                        throw new IllegalArgumentException(String.format("Invalid DishCode: '%s'", s), ex);
                    }
                })
                .toList();

        if (!wanted.isEmpty()) {
            predicates.add(path.in(wanted));
        }
    }


    private void addEqualPredicateForLong(String value, Path<Long> path, CriteriaBuilder builder, List<Predicate> predicates) {
        if (value != null && !value.isEmpty())
            predicates.add(builder.equal(path, Long.valueOf(value)));
    }

    private void addLikePredicate(String value, Path<String> path, CriteriaBuilder builder, List<Predicate> predicates) {
        if (value != null && !value.isEmpty())
            predicates.add(builder.like(builder.lower(path), "%" + value.toLowerCase() + "%"));
    }

    private void addEqualPredicateForBigDecimal(String value, Path<BigDecimal> path, CriteriaBuilder builder, List<Predicate> predicates) {
        if (value != null && !value.isEmpty())
            predicates.add(builder.equal(path, new java.math.BigDecimal(value)));
    }

    private void addEqualPredicateForInteger(String value, Path<Integer> path, CriteriaBuilder builder, List<Predicate> predicates) {
        if (value != null && !value.isEmpty())
            predicates.add(builder.equal(path, Integer.valueOf(value)));
    }

    private List<Order> getOrderList(Pageable pageable, CriteriaBuilder builder, Root<Dish> root) {
        List<Order> orders = new ArrayList<>();
        pageable.getSort().forEach(order -> {
            String property = order.getProperty();
            Path<?> path = resolvePath(property, root);
            if (path != null)
                orders.add(order.isAscending() ? builder.asc(path) : builder.desc(path));
        });
        return orders;
    }

    private Path<?> resolvePath(String property, Root<Dish> root) {
        Path<?> path = root;
        switch (property) {
            case "id":
                path = root.get("id");
                break;
            case "name":
                path = root.get("name");
                break;
            case PRICE:
                path = root.get(PRICE);
                break;
            case STOCK:
                path = root.get(STOCK);
                break;
            case "code":
                path = root.get("code");
                break;
            default:
                String[] parts = property.split("\\.");
                for (String part : parts) {
                    path = path.get(part);
                }
                break;
        }
        return path;
    }

    private void addDateRangePredicate(String dateFrom, String dateTo, CriteriaBuilder builder, Path<LocalDateTime> path, List<Predicate> predicates) {
        if (dateFrom != null && dateTo == null)
            predicates.add(builder.greaterThanOrEqualTo(path, parseDate(dateFrom).atStartOfDay()));
        else if (dateFrom == null && dateTo != null)
            predicates.add(builder.lessThanOrEqualTo(path, parseDate(dateTo).atTime(LocalTime.MAX)));
        else if (dateFrom != null)
            predicates.add(builder.between(path, parseDate(dateFrom).atStartOfDay(), parseDate(dateTo).atTime(LocalTime.MAX)));
    }

    private LocalDate parseDate(String date) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, dateFormatter);
    }

    private long getTotalCount(DishFilterDTO filter, CriteriaBuilder builder) {
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<Dish> countRoot = countQuery.from(Dish.class);
        List<Predicate> predicates = buildPredicates(filter, builder, countRoot);
        countQuery.select(builder.count(countRoot)).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(countQuery).getSingleResult();
    }

}