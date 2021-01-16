package fitnessstudio.statistics.record;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QRestockActivity is a Querydsl query type for RestockActivity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QRestockActivity extends EntityPathBase<RestockActivity> {

    private static final long serialVersionUID = -1684879824L;

    public static final QRestockActivity restockActivity = new QRestockActivity("restockActivity");

    public final NumberPath<Integer> amount = createNumber("amount", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ComparablePath<org.javamoney.moneta.Money> price = createComparable("price", org.javamoney.moneta.Money.class);

    public final StringPath product = createString("product");

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public QRestockActivity(String variable) {
        super(RestockActivity.class, forVariable(variable));
    }

    public QRestockActivity(Path<? extends RestockActivity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRestockActivity(PathMetadata metadata) {
        super(RestockActivity.class, metadata);
    }

}

