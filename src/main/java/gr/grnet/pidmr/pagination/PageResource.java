package gr.grnet.pidmr.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;

@Schema(name="PageResource", description="An object represents the paginated entities.")
public class PageResource<R> {

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Integer.class,
            description = "Page size.",
            example = "10"
    )
    @JsonProperty("size_of_page")
    private int sizeOfPage;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Integer.class,
            description = "Page number.",
            example = "1"
    )
    @JsonProperty("number_of_page")
    private int numberOfPage;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Long.class,
            description = "Total elements.",
            example = "1"
    )
    @JsonProperty("total_elements")
    private long totalElements;

    @Schema(
            type = SchemaType.NUMBER,
            implementation = Integer.class,
            description = "Total pages.",
            example = "1"
    )
    @JsonProperty("total_pages")
    private int totalPages;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = Object.class,
            description = "Paginated entities."
    )
    @JsonProperty("content")
    private List<R> content;

    @Schema(
            type = SchemaType.ARRAY,
            implementation = PageLink.class,
            description = "Link to paginated entities."
    )
    @JsonProperty("links")
    private  List<PageLink> links;

    public PageResource() {
    }

    public PageResource(Pageable<R> pageable, UriInfo uriInfo){

        links = new ArrayList<>();
        this.content = pageable.list();
        this.sizeOfPage = pageable.page().size;
        this.numberOfPage = pageable.page().index+1;
        this.totalElements = pageable.count();
        this.totalPages = pageable.pageCount();

        if(totalPages !=1){
            links.add(buildPageLink(uriInfo, 1, sizeOfPage, "first"));
            links.add(buildPageLink(uriInfo, totalPages, sizeOfPage, "last"));
            links.add(buildPageLink(uriInfo, numberOfPage, sizeOfPage, "self"));


            if(pageable.hasPreviousPage() && pageable.list().size()!=0) {
                links.add(buildPageLink(uriInfo, numberOfPage -1, sizeOfPage, "prev"));
            }

            if(pageable.hasNextPage()) {
                links.add(buildPageLink(uriInfo, numberOfPage +1, sizeOfPage, "next"));
            }
        }
    }


    private PageLink buildPageLink(UriInfo uriInfo, int page, int size, String rel) {

        return PageLink
                .builder()
                .href(uriInfo.getRequestUriBuilder().replaceQueryParam("page", page).replaceQueryParam("size", size).build().toString())
                .rel(rel)
                .build();
    }

    public int getSizeOfPage() {
        return sizeOfPage;
    }

    public void setSizeOfPage(int sizeOfPage) {
        this.sizeOfPage = sizeOfPage;
    }

    public int getNumberOfPage() {
        return numberOfPage;
    }

    public void setNumberOfPage(int numberOfPage) {
        this.numberOfPage = numberOfPage;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<R> getContent() {
        return content;
    }

    public void setContent(List<R> content) {
        this.content = content;
    }

    public List<PageLink> getLinks() {
        return links;
    }

    public void setLinks(List<PageLink> links) {
        this.links = links;
    }
}
