package site.itcp.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@Accessors(chain = true)
public class BasicEntity {
    /**
     * 记录id
     */
    @Id
    @Column(name = "ID")
    private Long id;
    /**
     * 创建人id
     */
    @Column(name = "CREATE_ID")
    private Long createId;
    /**
     * 创建时间
     */

    @Column(name = "CREATE_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 修改人id
     */
    @Column(name = "MODIFY_ID")
    private Long modifyId;
    /**
     * 修改时间
     */
    @Column(name = "MODIFY_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyTime;
    /**
     * 是否有效
     */
    @Column(name = "VALID")
    private Integer valid;

    /**
     * 排序
     */
    @Column(name = "SORT")
    private Long sort;
}
