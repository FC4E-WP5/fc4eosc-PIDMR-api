package gr.grnet.pidmr.pagination;

import java.util.List;

public interface Pageable<Entity> {

    int pageCount();

    Page page();

    long count();

    <T extends Entity> List<T> list();

    boolean hasPreviousPage();

    boolean hasNextPage();
}
