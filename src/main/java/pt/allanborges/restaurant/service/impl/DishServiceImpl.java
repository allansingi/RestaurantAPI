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
import pt.allanborges.restaurant.model.entities.DishCode;
import pt.allanborges.restaurant.model.mapper.DishMapper;
import pt.allanborges.restaurant.repository.DishRepository;
import pt.allanborges.restaurant.service.DishCodeService;
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
    private final DishCodeService dishCodeService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public DishDTO createDish(final DishDTO dto) {
        Dish entity = dishMapper.toEntity(dto);
        var codeDto = dto.getCode();
        if (codeDto == null || codeDto.getCode() == null || codeDto.getCode().isBlank())
            throw new IllegalArgumentException("Dish code is required");

        var code = dishCodeService.resolveOrCreate(codeDto.getCode(), codeDto.getDescription());
        entity.setCode(code);
        return dishMapper.toDTO(dishRepository.save(entity));
    }

    public List<DishDTO> findAllDishes() {
        var active = dishRepository.findByInactivatedDateIsNull();
        return dishMapper.toDTOList(active);
    }

    @Override
    public DishDTO getDishById(final Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found. Id: " + id));
        return dishMapper.toDTO(dish);
    }

    @Override
    @Transactional
    public DishDTO updateDish(final Long id, final DishDTO dto) {
        Dish current = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found. Id: " + id));
        dishMapper.updateEntityFromDTO(dto, current);
        if (dto.getCode() != null && dto.getCode().getCode() != null && !dto.getCode().getCode().isBlank()) {
            var code = dishCodeService.resolveOrCreate(dto.getCode().getCode(), dto.getCode().getDescription());
            current.setCode(code);
        }
        return dishMapper.toDTO(dishRepository.save(current));
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
        return new PageImpl<>(response.getContent(), pageable, response.getTotalElements());
    }

    // --------- Criteria filtering ----------

    @Transactional
    public Page<Dish> findAllDishesWithFilters(DishFilterDTO filter, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Dish> cq = cb.createQuery(Dish.class);
        Root<Dish> root = cq.from(Dish.class);
        List<Predicate> predicates = buildPredicates(filter, cb, root);
        cq.where(predicates.toArray(new Predicate[0]));

        if (pageable.getSort().isSorted()) {
            cq.orderBy(getOrderList(pageable, cb, root));
        }

        List<Dish> result = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = getTotalCount(filter, cb);
        return new PageImpl<>(result, pageable, total);
    }

    private List<Predicate> buildPredicates(DishFilterDTO filter, CriteriaBuilder cb, Root<Dish> root) {
        List<Predicate> predicates = new ArrayList<>();
        // only active
        predicates.add(cb.isNull(root.get("inactivatedDate")));

        addEqualPredicateForLong(filter.getId(), root.get("id"), cb, predicates);
        addLikePredicate(filter.getName(), root.get("name"), cb, predicates);
        addLikePredicate(filter.getDescription(), root.get("description"), cb, predicates);
        addEqualPredicateForBigDecimal(filter.getPrice(), root.get(PRICE), cb, predicates);
        addEqualPredicateForInteger(filter.getStock(), root.get(STOCK), cb, predicates);
        addInPredicateForCodes(filter.getCode(), root, cb, predicates);
        addDateRangePredicate(filter.getCreatedDateFrom(), filter.getCreatedDateTo(), cb, root.get("createdDate"), predicates);
        return predicates;
    }

    private void addInPredicateForCodes(List<String> rawCodes,
                                        Root<Dish> root,
                                        CriteriaBuilder cb,
                                        List<Predicate> predicates) {
        if (rawCodes == null || rawCodes.isEmpty()) return;

        // Normalize: split commas, trim, uppercase, distinct
        List<String> codes = rawCodes.stream()
                .filter(Objects::nonNull)
                .flatMap(s -> Arrays.stream(s.split(",")))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .distinct()
                .toList();

        if (codes.isEmpty()) return;

        Join<Dish, DishCode> codeJoin = root.join("code");
        CriteriaBuilder.In<String> in = cb.in(cb.lower(codeJoin.get("code")));
        codes.forEach(c -> in.value(c.toLowerCase()));
        predicates.add(in);
    }

    private void addEqualPredicateForLong(String value, Path<Long> path, CriteriaBuilder cb, List<Predicate> predicates) {
        if (value != null && !value.isEmpty()) predicates.add(cb.equal(path, Long.valueOf(value)));
    }

    private void addLikePredicate(String value, Path<String> path, CriteriaBuilder cb, List<Predicate> predicates) {
        if (value != null && !value.isEmpty()) predicates.add(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
    }

    private void addEqualPredicateForBigDecimal(String value, Path<BigDecimal> path, CriteriaBuilder cb, List<Predicate> predicates) {
        if (value != null && !value.isEmpty()) predicates.add(cb.equal(path, new BigDecimal(value)));
    }

    private void addEqualPredicateForInteger(String value, Path<Integer> path, CriteriaBuilder cb, List<Predicate> predicates) {
        if (value != null && !value.isEmpty()) predicates.add(cb.equal(path, Integer.valueOf(value)));
    }

    private List<Order> getOrderList(Pageable pageable, CriteriaBuilder cb, Root<Dish> root) {
        List<Order> orders = new ArrayList<>();
        pageable.getSort().forEach(order -> {
            String prop = order.getProperty();
            Path<?> p = resolvePath(prop, root);
            if (p != null) orders.add(order.isAscending() ? cb.asc(p) : cb.desc(p));
        });
        return orders;
    }

    private Path<?> resolvePath(String property, Root<Dish> root) {
        switch (property) {
            case "id":    return root.get("id");
            case "name":  return root.get("name");
            case PRICE:   return root.get(PRICE);
            case STOCK:   return root.get(STOCK);
            case "code":  // sort by code text via join
                return root.join("code").get("code");
            default:
                Path<?> p = root;
                for (String part : property.split("\\.")) p = p.get(part);
                return p;
        }
    }

    private void addDateRangePredicate(String dateFrom, String dateTo, CriteriaBuilder cb, Path<LocalDateTime> path, List<Predicate> predicates) {
        if (dateFrom != null && dateTo == null)
            predicates.add(cb.greaterThanOrEqualTo(path, parseDate(dateFrom).atStartOfDay()));
        else if (dateFrom == null && dateTo != null)
            predicates.add(cb.lessThanOrEqualTo(path, parseDate(dateTo).atTime(LocalTime.MAX)));
        else if (dateFrom != null)
            predicates.add(cb.between(path, parseDate(dateFrom).atStartOfDay(), parseDate(dateTo).atTime(LocalTime.MAX)));
    }

    private LocalDate parseDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private long getTotalCount(DishFilterDTO filter, CriteriaBuilder cb) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Dish> countRoot = countQuery.from(Dish.class);
        List<Predicate> predicates = buildPredicates(filter, cb, countRoot);
        countQuery.select(cb.count(countRoot)).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(countQuery).getSingleResult();
    }

}