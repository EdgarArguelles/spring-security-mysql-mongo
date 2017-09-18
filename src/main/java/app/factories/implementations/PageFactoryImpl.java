package app.factories.implementations;

import app.exceptions.AppValidationException;
import app.factories.PageFactory;
import app.pojos.pages.FilterRequest;
import app.pojos.pages.PageDataRequest;
import app.pojos.pages.PageDataResponse;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class PageFactoryImpl implements PageFactory {

    private final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private final String DATE_PATTERN = "yyyy-MM-dd";

    public interface OPERATION {
        String EQ = "EQ";
        String NE = "NE";
        String GT = "GT";
        String GET = "GET";
        String LT = "LT";
        String LET = "LET";
        String LIKE = "LIKE";
        String STARTSWITH = "STARTSWITH";
        String ENDSWITH = "ENDSWITH";
    }

    static List<String> operationsAllowed;

    static {
        operationsAllowed = new ArrayList<>();

        //iterate all interface properties
        Arrays.asList(OPERATION.class.getDeclaredFields()).forEach((field -> {
            try {
                operationsAllowed.add((String) field.get(String.class));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public PageRequest pageRequest(PageDataRequest pageDataRequest) {
        Sort sort = null;

        Sort.Direction direction = getDirection(pageDataRequest.getDirection());
        if (pageDataRequest.getSort() != null && !pageDataRequest.getSort().isEmpty()) {
            sort = new Sort(direction, pageDataRequest.getSort());
        }

        cleanData(pageDataRequest, sort, direction);
        return new PageRequest(pageDataRequest.getPage(), pageDataRequest.getSize(), sort);
    }

    @Override
    public Specifications getSpecifications(List<FilterRequest> filtersRequest) {
        if (filtersRequest == null) {
            return null;
        }

        final List<Specification> specifications = new ArrayList<>();
        filtersRequest.forEach(fr -> specifications.add((root, query, cb) -> getPredicate(fr, root, cb)));

        Specifications where = null;
        for (Specification s : specifications) {
            if (where == null) {
                where = Specifications.where(s);
            } else {
                where = where.and(s);
            }
        }

        return where;
    }

    @Override
    public Predicate getPredicate(List<FilterRequest> filtersRequest, EntityPathBase entityPathBase) {
        if (filtersRequest == null) {
            return null;
        }

        BooleanExpression expression = null;
        for (FilterRequest fr : filtersRequest) {
            if (expression == null) {
                expression = getBooleanExpression(fr, entityPathBase);
            } else {
                expression = expression.and(getBooleanExpression(fr, entityPathBase));
            }
        }
        return expression;
    }

    @Override
    public PageDataResponse pageResponse(Page page, PageDataRequest pageDataRequest) {
        return new PageDataResponse(page.getTotalPages(), page.getTotalElements(), pageDataRequest);
    }

    /**
     * Create a Predicate from FilterRequest
     *
     * @param filterRequest Filter data
     * @param root          A root type in the from clause
     * @param cb            Used to construct criteria queries
     * @return Predicate generated
     */
    private javax.persistence.criteria.Predicate getPredicate(FilterRequest filterRequest, Root<Object> root, CriteriaBuilder cb) {
        filterRequest.setOperation(filterRequest.getOperation() != null ? filterRequest.getOperation().toUpperCase() : OPERATION.EQ);
        if (!operationsAllowed.contains(filterRequest.getOperation())) {
            // default operation
            filterRequest.setOperation(OPERATION.EQ);
        }

        String stringValue = filterRequest.getValue();
        LocalDateTime dateTimeValue = null;
        LocalDate dateValue = null;
        try {
            dateTimeValue = LocalDateTime.parse(stringValue, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        } catch (Exception e) {
        }
        try {
            dateValue = LocalDate.parse(stringValue, DateTimeFormatter.ofPattern(DATE_PATTERN));
        } catch (Exception e) {
        }

        Object value = dateTimeValue != null ? dateTimeValue
                : dateValue != null ? dateValue
                : stringValue;

        switch (filterRequest.getOperation()) {
            case OPERATION.EQ:
                return cb.equal(root.get(filterRequest.getField()), value);
            case OPERATION.NE:
                return cb.notEqual(root.get(filterRequest.getField()), value);
            case OPERATION.GT:
                if (dateTimeValue != null) {
                    return cb.greaterThan(root.get(filterRequest.getField()), dateTimeValue);
                }
                if (dateValue != null) {
                    return cb.greaterThan(root.get(filterRequest.getField()), dateValue);
                }
                return cb.greaterThan(root.get(filterRequest.getField()), stringValue);
            case OPERATION.GET:
                if (dateTimeValue != null) {
                    return cb.greaterThanOrEqualTo(root.get(filterRequest.getField()), dateTimeValue);
                }
                if (dateValue != null) {
                    return cb.greaterThanOrEqualTo(root.get(filterRequest.getField()), dateValue);
                }
                return cb.greaterThanOrEqualTo(root.get(filterRequest.getField()), stringValue);
            case OPERATION.LT:
                if (dateTimeValue != null) {
                    return cb.lessThan(root.get(filterRequest.getField()), dateTimeValue);
                }
                if (dateValue != null) {
                    return cb.lessThan(root.get(filterRequest.getField()), dateValue);
                }
                return cb.lessThan(root.get(filterRequest.getField()), stringValue);
            case OPERATION.LET:
                if (dateTimeValue != null) {
                    return cb.lessThanOrEqualTo(root.get(filterRequest.getField()), dateTimeValue);
                }
                if (dateValue != null) {
                    return cb.lessThanOrEqualTo(root.get(filterRequest.getField()), dateValue);
                }
                return cb.lessThanOrEqualTo(root.get(filterRequest.getField()), stringValue);
            case OPERATION.STARTSWITH:
                return cb.like(root.get(filterRequest.getField()), stringValue + "%");
            case OPERATION.ENDSWITH:
                return cb.like(root.get(filterRequest.getField()), "%" + stringValue);
            case OPERATION.LIKE:
            default:
                return cb.like(root.get(filterRequest.getField()), "%" + stringValue + "%");
        }
    }

    /**
     * Create a BooleanExpression from FilterRequest
     *
     * @param filterRequest  Filter data
     * @param entityPathBase QEntity base to generate BooleanExpression
     * @return BooleanExpression generated
     */
    private BooleanExpression getBooleanExpression(FilterRequest filterRequest, EntityPathBase entityPathBase) {
        filterRequest.setOperation(filterRequest.getOperation() != null ? filterRequest.getOperation().toUpperCase() : OPERATION.EQ);
        if (!operationsAllowed.contains(filterRequest.getOperation())) {
            // default operation
            filterRequest.setOperation(OPERATION.EQ);
        }

        try {
            Class type = entityPathBase.getClass().getDeclaredField(filterRequest.getField()).getType();
            PathBuilder entityPath = new PathBuilder(entityPathBase.getClass(), entityPathBase.toString());
            if (type == NumberPath.class) {
                return getNumberExpression(filterRequest, entityPath);
            }
            if (type == DateTimePath.class) {
                return getDateTimeExpression(filterRequest, entityPath);
            }
            if (type == DatePath.class) {
                return getDateExpression(filterRequest, entityPath);
            }
            return getStringExpression(filterRequest, entityPath);
        } catch (Exception e) {
            throw new AppValidationException(e.getMessage());
        }
    }

    /**
     * Create a BooleanExpression from FilterRequest when field is String
     *
     * @param filterRequest Filter data
     * @param entityPath    PathBuilder base to generate BooleanExpression
     * @return BooleanExpression generated
     */
    private BooleanExpression getStringExpression(FilterRequest filterRequest, PathBuilder entityPath) {
        StringPath expression = entityPath.getString(filterRequest.getField());
        switch (filterRequest.getOperation()) {
            case OPERATION.EQ:
                return expression.eq(filterRequest.getValue());
            case OPERATION.NE:
                return expression.ne(filterRequest.getValue());
            case OPERATION.GT:
                return expression.gt(filterRequest.getValue());
            case OPERATION.GET:
                return expression.goe(filterRequest.getValue());
            case OPERATION.LT:
                return expression.lt(filterRequest.getValue());
            case OPERATION.LET:
                return expression.loe(filterRequest.getValue());
            case OPERATION.STARTSWITH:
                return expression.like(filterRequest.getValue() + "%");
            case OPERATION.ENDSWITH:
                return expression.like("%" + filterRequest.getValue());
            case OPERATION.LIKE:
            default:
                return expression.like("%" + filterRequest.getValue() + "%");
        }
    }

    /**
     * Create a BooleanExpression from FilterRequest when field is Number
     *
     * @param filterRequest Filter data
     * @param entityPath    PathBuilder base to generate BooleanExpression
     * @return BooleanExpression generated
     */
    private BooleanExpression getNumberExpression(FilterRequest filterRequest, PathBuilder entityPath) {
        NumberPath expression = entityPath.getNumber(filterRequest.getField(), Number.class);
        Number numberValue = Double.parseDouble(filterRequest.getValue());
        switch (filterRequest.getOperation()) {
            case OPERATION.EQ:
                return expression.eq(numberValue);
            case OPERATION.NE:
                return expression.ne(numberValue);
            case OPERATION.GT:
                return expression.gt(numberValue);
            case OPERATION.GET:
                return expression.goe(numberValue);
            case OPERATION.LT:
                return expression.lt(numberValue);
            case OPERATION.LET:
                return expression.loe(numberValue);
            case OPERATION.STARTSWITH:
            case OPERATION.ENDSWITH:
            case OPERATION.LIKE:
            default:
                throw new AppValidationException("Number type doesn't allow like operations.");
        }
    }

    /**
     * Create a BooleanExpression from FilterRequest when field is DateTime
     *
     * @param filterRequest Filter data
     * @param entityPath    PathBuilder base to generate BooleanExpression
     * @return BooleanExpression generated
     */
    private BooleanExpression getDateTimeExpression(FilterRequest filterRequest, PathBuilder entityPath) {
        DateTimePath expression = entityPath.getDateTime(filterRequest.getField(), LocalDateTime.class);
        LocalDateTime dateTimeValue = LocalDateTime.parse(filterRequest.getValue(), DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        switch (filterRequest.getOperation()) {
            case OPERATION.EQ:
                return expression.eq(dateTimeValue);
            case OPERATION.NE:
                return expression.ne(dateTimeValue);
            case OPERATION.GT:
                return expression.gt(dateTimeValue);
            case OPERATION.GET:
                return expression.goe(dateTimeValue);
            case OPERATION.LT:
                return expression.lt(dateTimeValue);
            case OPERATION.LET:
                return expression.loe(dateTimeValue);
            case OPERATION.STARTSWITH:
            case OPERATION.ENDSWITH:
            case OPERATION.LIKE:
            default:
                throw new AppValidationException("DateTime type doesn't allow like operations.");
        }
    }

    /**
     * Create a BooleanExpression from FilterRequest when field is Date
     *
     * @param filterRequest Filter data
     * @param entityPath    PathBuilder base to generate BooleanExpression
     * @return BooleanExpression generated
     */
    private BooleanExpression getDateExpression(FilterRequest filterRequest, PathBuilder entityPath) {
        DatePath expression = entityPath.getDate(filterRequest.getField(), LocalDate.class);
        LocalDate dateValue = LocalDate.parse(filterRequest.getValue(), DateTimeFormatter.ofPattern(DATE_PATTERN));
        switch (filterRequest.getOperation()) {
            case OPERATION.EQ:
                return expression.eq(dateValue);
            case OPERATION.NE:
                return expression.ne(dateValue);
            case OPERATION.GT:
                return expression.gt(dateValue);
            case OPERATION.GET:
                return expression.goe(dateValue);
            case OPERATION.LT:
                return expression.lt(dateValue);
            case OPERATION.LET:
                return expression.loe(dateValue);
            case OPERATION.STARTSWITH:
            case OPERATION.ENDSWITH:
            case OPERATION.LIKE:
            default:
                throw new AppValidationException("Date type doesn't allow like operations.");
        }
    }

    /**
     * Parse Sort Direction (is not case sensitive)
     *
     * @param direction page sort direction (could be ASC or DESC)
     * @return Sort Direction or null if value was invalid
     */
    private Sort.Direction getDirection(String direction) {
        Sort.Direction sortDirection =
                direction == null ? null :
                        direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC :
                                direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC :
                                        null;

        return sortDirection;
    }

    /**
     * Clean PageDataRequest values
     *
     * @param pageDataRequest original vales
     * @param sort            generated sort
     * @param direction       generated direction
     */
    private void cleanData(PageDataRequest pageDataRequest, Sort sort, Sort.Direction direction) {
        pageDataRequest.setPage(pageDataRequest.getPage() != null && pageDataRequest.getPage() > 0 ? pageDataRequest.getPage() : 0);
        pageDataRequest.setSize(pageDataRequest.getSize() != null && pageDataRequest.getSize() > 1 ? pageDataRequest.getSize() : 1);

        if (sort == null) {
            pageDataRequest.setDirection(null);
            pageDataRequest.setSort(null);
        } else {
            pageDataRequest.setDirection(direction == Sort.Direction.DESC ? "DESC" : "ASC");
        }
    }
}