package gr.grnet.pidmr.pagination;

import java.util.ArrayList;
import java.util.List;

public class PageableImpl<Entity> implements Pageable<Entity>{

    public List<Entity> list;

    public int index;

    public long count;

    public int size;

    public Page page;

    @Override
    public int pageCount() {
        long count = this.count;
        return count == 0L ? 1 : (int)Math.ceil((double)count / (double)this.size);
    }

    @Override
    public Page page() {
        return page;
    }

    @Override
    public long count() {
        return count;
    }

    @Override
    public <T extends Entity> List<T> list() {
        List<T> documents = new ArrayList<>();

        list.stream().forEach(entity -> documents.add((T) entity));

        return documents;
    }

    @Override
    public boolean hasPreviousPage() {
        return this.index > 0;
    }

    @Override
    public boolean hasNextPage() {
        return this.index < this.pageCount() - 1;
    }
}
