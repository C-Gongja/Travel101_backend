package com.sharavel.sharavel_be.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sharavel.sharavel_be.follow.entity.UserFollow;
import com.sharavel.sharavel_be.socialLink.entity.SocialLink;
import com.sharavel.sharavel_be.trip.entity.Trip;
import com.sharavel.sharavel_be.user.repository.UserRepository;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Table(name = "users")
@Entity
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private final Long id;

	@Column(nullable = false, unique = true, updatable = false)
	private final String uuid;

	@Column(nullable = true)
	private String password;

	@Column(nullable = true)
	private String provider;

	@Column(nullable = true)
	private String providerId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = true)
	private String username;

	@Column(unique = true, length = 100, nullable = false)
	private String email;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SocialLink> socialLinks = new ArrayList<>();

	@Column(nullable = true)
	private String picture;

	@Column(nullable = true)
	private String country;

	@Column(nullable = false)
	private Integer totalTrips;

	@Column(nullable = false)
	private Integer totalTripDays;

	@Column(columnDefinition = "TEXT", nullable = true)
	private String bio;

	@OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserFollow> followers = new HashSet<>();

	@OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserFollow> following = new HashSet<>();

	@OneToMany(mappedBy = "uid", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Trip> trips = new ArrayList<>();

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private Set<Roles> roles = new HashSet<>();

	@CreationTimestamp
	@Column(updatable = false, name = "created_at")
	private final LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	protected Users() {
		this.id = null;
		this.uuid = null;
		this.email = null;
		this.name = null;
		this.username = null;
		this.totalTrips = null;
		this.totalTripDays = null;
		this.createdAt = null;
	}

	// Builder용 생성자
	private Users(Builder builder) {
		this.id = null; // assigned by JPA
		this.uuid = UUID.randomUUID().toString();
		this.password = builder.password;
		this.email = Objects.requireNonNull(builder.email, "Email is required");
		this.name = Objects.requireNonNull(builder.name, "Name is required");
		this.username = Objects.requireNonNull(builder.username, "Username is required");
		this.provider = builder.provider;
		this.providerId = builder.providerId;
		this.picture = builder.picture;
		this.country = builder.country;
		this.totalTrips = builder.totalTrips;
		this.totalTripDays = builder.totalTripDays;
		this.bio = builder.bio;
		this.socialLinks = builder.socialLinks != null ? new ArrayList<>(builder.socialLinks) : new ArrayList<>();
		this.followers = builder.followers != null ? new HashSet<>(builder.followers) : new HashSet<>();
		this.following = builder.following != null ? new HashSet<>(builder.following) : new HashSet<>();
		this.trips = builder.trips != null ? new ArrayList<>(builder.trips) : new ArrayList<>();
		this.roles = builder.roles != null ? new HashSet<>(builder.roles) : new HashSet<>();
		this.createdAt = null; // assigned by JPA
	}

	// Builder 패턴
	public static class Builder {
		private String password;
		private String email;
		private String name;
		private String username;
		private String provider;
		private String providerId;
		private String picture;
		private String country;
		private Integer totalTrips = 0;
		private Integer totalTripDays = 0;
		private String bio;
		private List<SocialLink> socialLinks;
		private Set<UserFollow> followers;
		private Set<UserFollow> following;
		private List<Trip> trips;
		private Set<Roles> roles;

		public Builder(String email, String name, String username, Integer totalTrips, Integer totalTripDays) {
			this.email = email;
			this.name = name;
			this.username = username;
			this.totalTrips = totalTrips;
			this.totalTripDays = totalTripDays;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public Builder provider(String provider) {
			this.provider = provider;
			return this;
		}

		public Builder providerId(String providerId) {
			this.providerId = providerId;
			return this;
		}

		public Builder picture(String picture) {
			this.picture = picture;
			return this;
		}

		public Builder country(String country) {
			this.country = country;
			return this;
		}

		public Builder totalTrips(Integer totalTrips) {
			this.totalTrips = totalTrips;
			return this;
		}

		public Builder totalTripDays(Integer totalTripDays) {
			this.totalTripDays = totalTripDays;
			return this;
		}

		public Builder bio(String bio) {
			this.bio = bio;
			return this;
		}

		public Builder socialLinks(List<SocialLink> socialLinks) {
			this.socialLinks = socialLinks;
			return this;
		}

		public Builder followers(Set<UserFollow> followers) {
			this.followers = followers;
			return this;
		}

		public Builder following(Set<UserFollow> following) {
			this.following = following;
			return this;
		}

		public Builder trips(List<Trip> trips) {
			this.trips = trips;
			return this;
		}

		public Builder roles(Set<Roles> roles) {
			this.roles = roles;
			return this;
		}

		public Users build() {
			return new Users(this);
		}
	}

	// 캡슐화된 업데이트 메서드
	public void updateEmail(String email, String authenticatedUserId, UserRepository userRepository) {
		validateUser(authenticatedUserId);
		if (!isValidEmail(email)) {
			throw new IllegalArgumentException("Invalid email format");
		}
		if (userRepository.existsByEmail(email)) {
			throw new IllegalArgumentException("Email '" + email + "' already exists");
		}
		this.email = email;
	}

	public void updateName(String name, String authenticatedUserId) {
		validateUser(authenticatedUserId);
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Name must not be empty");
		}
		this.name = name;
	}

	public void updateUsername(String username, String authenticatedUserId, UserRepository userRepository) {
		validateUser(authenticatedUserId);
		if (username != null && userRepository.existsByUsername(username)) {
			throw new IllegalArgumentException("Username '" + username + "' already exists");
		}
		this.username = username;
	}

	public void updateProvider(String provider, String authenticatedUserId) {
		validateUser(authenticatedUserId);
		this.provider = provider;
	}

	public void updateProviderId(String providerId, String authenticatedUserId) {
		validateUser(authenticatedUserId);
		this.providerId = providerId;
	}

	public void updatePicture(String picture, String authenticatedUserId) {
		validateUser(authenticatedUserId);
		this.picture = picture;
	}

	public void updateCountry(String country, String authenticatedUserId) {
		validateUser(authenticatedUserId);
		this.country = country;
	}

	public void updateTotalTrips(Integer totalTrips, String authenticatedUserId) {
		validateUser(authenticatedUserId);
		if (totalTrips == null || totalTrips < 0) {
			throw new IllegalArgumentException("Total trips must be non-negative");
		}
		this.totalTrips = totalTrips;
	}

	public void updateTotalTripDays(Integer totalTripDays, String authenticatedUserId) {
		validateUser(authenticatedUserId);
		if (totalTripDays == null || totalTripDays < 0) {
			throw new IllegalArgumentException("Total trip days must be non-negative");
		}
		this.totalTripDays = totalTripDays;
	}

	public void updateBio(String bio, String authenticatedUserId) {
		validateUser(authenticatedUserId);
		if (bio != null && bio.length() > 500) {
			throw new IllegalArgumentException("Bio must not exceed 500 characters");
		}
		this.bio = bio;
	}

	public void updateSocialLinks(List<SocialLink> socialLinks, String authenticatedUserId) {
		validateUser(authenticatedUserId);
		this.socialLinks.clear();
		if (socialLinks != null) {
			this.socialLinks.addAll(socialLinks);
		}
	}

	public void updateFollowers(Set<UserFollow> followers, String authenticatedUserId) {
		validateUser(authenticatedUserId);
		this.followers.clear();
		if (followers != null) {
			this.followers.addAll(followers);
		}
	}

	public void updateFollowing(Set<UserFollow> following, String authenticatedUserId) {
		validateUser(authenticatedUserId);
		this.following.clear();
		if (following != null) {
			this.following.addAll(following);
		}
	}

	public void updateTrips(List<Trip> trips, String authenticatedUserId) {
		validateUser(authenticatedUserId);
		this.trips.clear();
		if (trips != null) {
			this.trips.addAll(trips);
		}
	}

	public void updateRoles(Set<Roles> roles, String authenticatedUserId) {
		validateUser(authenticatedUserId);
		this.roles.clear();
		if (roles != null) {
			this.roles.addAll(roles);
		}
	}

	private void validateUser(String authenticatedUserId) {
		if (!this.uuid.equals(authenticatedUserId)) {
			throw new SecurityException("Unauthorized to update user");
		}
	}

	private boolean isValidEmail(String email) {
		return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
	}

	// Getter 메서드
	public Long getId() {
		return id;
	}

	public String getUuid() {
		return uuid;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getUsername() {
		return username;
	}

	public String getProvider() {
		return provider;
	}

	public String getProviderId() {
		return providerId;
	}

	public String getPicture() {
		return picture;
	}

	public String getCountry() {
		return country;
	}

	public Integer getTotalTrips() {
		return totalTrips;
	}

	public Integer getTotalTripDays() {
		return totalTripDays;
	}

	public String getBio() {
		return bio;
	}

	public List<SocialLink> getSocialLinks() {
		return new ArrayList<>(socialLinks); // 방어적 복사
	}

	public Set<UserFollow> getFollowers() {
		return new HashSet<>(followers); // 방어적 복사
	}

	public Set<UserFollow> getFollowing() {
		return new HashSet<>(following); // 방어적 복사
	}

	public List<Trip> getTrips() {
		return new ArrayList<>(trips); // 방어적 복사
	}

	public Set<Roles> getRoles() {
		return new HashSet<>(roles); // 방어적 복사
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}