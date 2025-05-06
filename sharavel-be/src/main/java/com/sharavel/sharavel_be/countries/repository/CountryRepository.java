package com.sharavel.sharavel_be.countries.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.countries.entity.Country;
import com.sharavel.sharavel_be.user.entity.Users;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
	Optional<Country> findByIso2(String iso2);

	Optional<Country> findByName(String name);

	@Query("SELECT DISTINCT c FROM Trip t JOIN t.countries c WHERE t.uid = :user AND t.isCompleted = true")
	List<Country> findDistinctCountriesByUser(@Param("user") Users user);
}
