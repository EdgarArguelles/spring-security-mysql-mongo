package app.factories;

import app.pojos.pages.FilterRequest;
import app.pojos.pages.PageDataRequest;
import app.pojos.pages.PageDataResponse;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specifications;

import java.util.List;

/**
 * Create Page instances
 */
public interface PageFactory {

    /**
     * Create a PageRequest from a PageDataRequest
     *
     * @param pageDataRequest PageDataRequest data
     * @return PageRequest created
     */
    PageRequest pageRequest(PageDataRequest pageDataRequest);

    /**
     * Create a Specifications instance from a FilterRequest list
     *
     * @param filtersRequest list of FilterRequest data
     * @return Specifications created
     */
    Specifications getSpecifications(List<FilterRequest> filtersRequest);

    /**
     * Create a Predicate instance from a FilterRequest list
     *
     * @param filtersRequest list of FilterRequest data
     * @param entityPathBase QEntity base to generate Predicate
     * @return Predicate created
     */
    Predicate getPredicate(List<FilterRequest> filtersRequest, EntityPathBase entityPathBase);

    /**
     * Create a PageDataResponse from a Page metadata
     *
     * @param page            Page metadata
     * @param pageDataRequest PageDataRequest data
     * @return PageDataResponse created
     */
    PageDataResponse pageResponse(Page page, PageDataRequest pageDataRequest);
}