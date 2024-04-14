
package acme.entities.contracts;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import acme.client.data.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ProgressLog extends AbstractEntity {

	// Serialisation identifier -----------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	//TODO: ver si el patron esta bien implementado
	@Column(unique = true)
	@Pattern(regexp = "^PG-[A-Z]{1,2}-[0-9]{4}$", message = "{validation.progressLog.code}")
	@NotBlank
	private String				recordId;

	//TODO: ver si el rango es de 0.00 a 100.00 o de 0.00 a 1.00
	//TODO: ver si el incremento tiene que ser estrictamente positivo respecto al anterior PG
	@DecimalMin(value = "0.00", inclusive = true)
	@DecimalMax(value = "100.00", inclusive = true)
	@Digits(integer = 3, fraction = 2)
	private double				completeness;

	@NotBlank
	@Length(max = 100)
	private String				comment;

	//TODO: esta hora tiene que ser posterior a la de creacion del contrato (servicio)
	@NotNull
	@PastOrPresent
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@NotBlank
	@Length(max = 75)
	private String				responsiblePerson;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@NotNull
	@Valid
	@ManyToOne(optional = false)
	private Contract			contract;

}
