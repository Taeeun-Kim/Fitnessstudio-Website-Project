package fitnessstudio.contracts;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QContract is a Querydsl query type for Contract
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QContract extends EntityPathBase<Contract> {

    private static final long serialVersionUID = 1558144271L;

    public static final QContract contract = new QContract("contract");

    public final StringPath description = createString("description");

    public final ComparablePath<org.javamoney.moneta.Money> fee = createComparable("fee", org.javamoney.moneta.Money.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath title = createString("title");

    public QContract(String variable) {
        super(Contract.class, forVariable(variable));
    }

    public QContract(Path<? extends Contract> path) {
        super(path.getType(), path.getMetadata());
    }

    public QContract(PathMetadata metadata) {
        super(Contract.class, metadata);
    }

}

