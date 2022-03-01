package io.legacyfighter.cabs.entity;

import io.legacyfighter.cabs.common.BaseEntity;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class ContractAttachment extends BaseEntity {

    public enum Status {
        PROPOSED, ACCEPTED_BY_ONE_SIDE, ACCEPTED_BY_BOTH_SIDES, REJECTED
    }

    ContractAttachment() {}

    @Column(nullable = false)
    private Instant creationDate = Instant.now();

    private Instant acceptedAt;

    private Instant rejectedAt;

    private Instant changeDate;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PROPOSED;

    @ManyToOne
    private Contract contract;

    ContractAttachment(Contract contract, Status status) {
        this.contract = contract;
        this.status = status;
    }

    static ContractAttachment proposed(Contract contract) {
        return new ContractAttachment(contract, Status.PROPOSED);
    }

    public void reject() {
        status = Status.REJECTED;
    }

    public void accept() {
        if (status.equals(Status.ACCEPTED_BY_ONE_SIDE) || status.equals(Status.ACCEPTED_BY_BOTH_SIDES)) {
            status = Status.ACCEPTED_BY_BOTH_SIDES;
        } else {
            status = ContractAttachment.Status.ACCEPTED_BY_ONE_SIDE;
        }
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public Instant getRejectedAt() {
        return rejectedAt;
    }

    public Instant getChangeDate() {
        return changeDate;
    }

    public Status getStatus() {
        return status;
    }

    public Contract getContract() {
        return contract;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ContractAttachment))
            return false;

        ContractAttachment other = (ContractAttachment) o;

        return this.getId() != null &&
                this.getId().equals(other.getId());
    }
}
