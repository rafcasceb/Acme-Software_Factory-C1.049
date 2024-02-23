
package acme.entities.sponsorships;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.URL;

import acme.client.data.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Invoice extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------------------------

	@Column(unique = true)
	@Pattern(regexp = "IN-[0-9]{4}-[0-9]{4}")
	@NotBlank
	private String				code;

	@Temporal(TemporalType.DATE)
	@PastOrPresent
	@NotNull
	private Date				registrationTime;

	@Temporal(TemporalType.DATE)
	@NotNull
	private Date				dueDate;

	@Min(1)
	@NotNull
	private double				quantity;

	@Min(0)
	@NotNull
	private double				tax;

	@URL
	private String				link;

	// Derived Attributes -------------------------------------------------------------------------------


	@Transient
	public Double getValue() {
		return this.tax + this.quantity;
	}

	// Validation  ------------------------------------------------------------
	@AssertTrue(message = "Due date must be at least one month ahead the registration time")
	public boolean isDueDateOneMonthAhead() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.registrationTime);
		cal.add(Calendar.MONTH, 1);

		return this.dueDate.after(cal.getTime());
	}

	// Constructor  ------------------------------------------------------------

	public Invoice(final String code, final Date registrationTime, final Date dueDate, final double quantity, final double tax, final String link) {
		this.code = code;
		this.registrationTime = registrationTime;
		this.dueDate = dueDate;
		this.quantity = quantity;
		this.tax = tax;
		this.link = link;

		if (!this.isDueDateOneMonthAhead())
			throw new IllegalArgumentException("Due date must be at least one month ahead of registration time");
	}


	// Relationships  ------------------------------------------------------------
	@NotNull
	@Valid
	@ManyToOne()
	private Sponsorship sponsorship;

}
