package site.itcp.base.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import site.itcp.core.entity.BasicEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = User.TABLE_NAME)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class User extends BasicEntity {
    public static final String TABLE_NAME = "T_USER";

    @Column(name = "NAME", length = 32)
    private String name;

    @Column(name = "ACCOUNT", length = 32)
    private String account;

    @Column(name = "PASSWORD", length = 64)
    private String password;

}
