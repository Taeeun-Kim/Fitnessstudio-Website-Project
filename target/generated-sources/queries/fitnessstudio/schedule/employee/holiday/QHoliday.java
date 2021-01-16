package fitnessstudio.schedule.employee.holiday;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHoliday is a Querydsl query type for Holiday
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QHoliday extends EntityPathBase<Holiday> {

    private static final long serialVersionUID = 650568873L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHoliday holiday = new QHoliday("holiday");

    public final fitnessstudio.schedule.entry.QScheduleEntry _super = new fitnessstudio.schedule.entry.QScheduleEntry(this);

    public final fitnessstudio.user.employee.QEmployee employee;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> end = _super.end;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> start = _super.start;

    public final EnumPath<fitnessstudio.schedule.request.RequestStatus> status = createEnum("status", fitnessstudio.schedule.request.RequestStatus.class);

    public QHoliday(String variable) {
        this(Holiday.class, forVariable(variable), INITS);
    }

    public QHoliday(Path<? extends Holiday> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHoliday(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHoliday(PathMetadata metadata, PathInits inits) {
        this(Holiday.class, metadata, inits);
    }

    public QHoliday(Class<? extends Holiday> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.employee = inits.isInitialized("employee") ? new fitnessstudio.user.employee.QEmployee(forProperty("employee"), inits.get("employee")) : null;
    }

}

