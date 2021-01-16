package fitnessstudio.schedule.entry;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QScheduleEntry is a Querydsl query type for ScheduleEntry
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QScheduleEntry extends EntityPathBase<ScheduleEntry> {

    private static final long serialVersionUID = -40500264L;

    public static final QScheduleEntry scheduleEntry = new QScheduleEntry("scheduleEntry");

    public final DateTimePath<java.time.LocalDateTime> end = createDateTime("end", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> start = createDateTime("start", java.time.LocalDateTime.class);

    public QScheduleEntry(String variable) {
        super(ScheduleEntry.class, forVariable(variable));
    }

    public QScheduleEntry(Path<? extends ScheduleEntry> path) {
        super(path.getType(), path.getMetadata());
    }

    public QScheduleEntry(PathMetadata metadata) {
        super(ScheduleEntry.class, metadata);
    }

}

