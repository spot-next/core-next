package at.spot.spring.web.dto;

import org.hibernate.validator.constraints.Email;

import com.google.gson.annotations.Expose;

public class UserDetails {
	@Email
	@Expose
	private String email;
	@Expose
	private String firstName;
	@Expose
	private String lastName;
	@Expose
	private String password;
	@Expose
	private String oldPassword;

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(final String oldPassword) {
		this.oldPassword = oldPassword;
	}
}
