package com.foton.okla.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foton.okla.model.Dish;

import java.util.List;
import java.util.Optional;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

	Optional<Dish> findById(Long id);

	List<Dish> findAll();

	Page<Dish> findAll(Pageable pageable);

	Page<Dish> findByCreatorId(Long id, Pageable pageable);

	@Query(value = "SELECT * FROM dish WHERE (:label = '' or UPPER(TRIM(label)) LIKE CONCAT('%',UPPER(TRIM(:label)),'%')) and (:type = '' or :type IN (SELECT types FROM dish_types where dish_id = id)) and (:ingredientsLen = 0 OR (id NOT IN (select distinct dish_id from dish_ingredients where ingredients NOT IN :ingredients)))", nativeQuery = true)
	Page<Dish> searchDishes(@Param("label") String label, @Param("type") String type,
			@Param("ingredients") List<String> ingredients, @Param("ingredientsLen") int ingredientsLen, Pageable pageable);
}
