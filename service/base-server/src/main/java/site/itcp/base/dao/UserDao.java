package site.itcp.base.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.itcp.base.entity.User;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

}
