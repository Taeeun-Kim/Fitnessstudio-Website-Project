package fitnessstudio.statistics.record;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCustomerActivity is a Querydsl query type for CustomerActivity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCustomerActivity extends EntityPathBase<CustomerActivity> {

    private static final long serialVersionUID = 2015506319L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCustomerActivity customerActivity = new QCustomerActivity("customerActivity");

    public final fitnessstudio.user.customer.QCustomer customer;

    public final BooleanPath direction = createBoolean("direction");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> timestamp = createDateTime("timestamp", java.time.LocalDateTime.class);

    public QCustomerActivity(String variable) {
        this(CustomerActivity.class, forVariable(variable), INITS);
    }

    public QCustomerActivity(Path<? extends CustomerActivity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCustomerActivity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCustomerActivity(PathMetadata metadata, PathInits inits) {
        this(CustomerActivity.class, metadata, inits);
    }

    public QCustomerActivity(Class<? extends CustomerActivity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.customer = inits.isInitialized("customer") ? new fitnessstudio.user.customer.QCustomer(forProperty("customer"), inits.get("customer")) : null;
    }

}

