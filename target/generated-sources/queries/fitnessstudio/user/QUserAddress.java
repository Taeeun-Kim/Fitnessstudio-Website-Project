package fitnessstudio.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserAddress is a Querydsl query type for UserAddress
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QUserAddress extends EntityPathBase<UserAddress> {

    private static final long serialVersionUID = 418252310L;

    public static final QUserAddress userAddress = new QUserAddress("userAddress");

    public final StringPath code = createString("code");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath location = createString("location");

    public final StringPath number = createString("number");

    public final StringPath street = createString("street");

    public QUserAddress(String variable) {
        super(UserAddress.class, forVariable(variable));
    }

    public QUserAddress(Path<? extends UserAddress> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserAddress(PathMetadata metadata) {
        super(UserAddress.class, metadata);
    }

}

