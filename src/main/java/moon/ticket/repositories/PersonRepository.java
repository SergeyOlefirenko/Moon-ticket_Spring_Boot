package moon.ticket.repositories;

import moon.ticket.datamodel.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findById(Long id);
    void deleteById(Long id);
    boolean existsByEmail(String email);
    List<Person>findByEmail(String email);
    long count(); // для общего счетчика всех пользователей
//Для выбора стран из списка
    @Query("SELECT DISTINCT p.country FROM Person p")
    List<String> findAllCountries();

    @Query("SELECT p.city, COUNT(p) FROM Person p WHERE p.country = :country GROUP BY p.city")
    List<Object[]> countPersonsByCityAndCountry(@Param("country") String country);

}
