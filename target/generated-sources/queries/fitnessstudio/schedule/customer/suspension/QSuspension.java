package fitnessstudio.schedule.customer.suspension;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSuspension is a Querydsl query type for Suspension
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSuspension extends EntityPathBase<Suspension> {

    private static final long serialVersionUID = -655272283L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSuspension suspension = new QSuspension("suspension");

    public final fitnessstudio.schedule.entry.QScheduleEntry _super = new fitnessstudio.schedule.entry.QScheduleEntry(this);

    public final fitnessstudio.user.customer.QCustomer customer;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> end = _super.end;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> start = _super.start;

    public QSuspension(String variable) {
        this(Suspension.class, forVariable(variable), INITS);
    }

    public QSuspension(Path<? extends Suspension> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSuspension(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSuspension(PathMetadata metadata, PathInits inits) {
        this(Suspension.class, metadata, inits);
    }

    public QSuspension(Class<? extends Suspension> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.customer = inits.isInitialized("customer") ? new fitnessstudio.user.customer.QCustomer(forProperty("customer"), inits.get("customer")) : null;
    }

}

