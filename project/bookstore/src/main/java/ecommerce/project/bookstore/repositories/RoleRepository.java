package ecommerce.project.bookstore.repositories;

import ecommerce.project.bookstore.entities.security.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

	Role findByName(String name);

}
