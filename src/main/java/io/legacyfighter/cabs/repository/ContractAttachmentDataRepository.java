package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.entity.ContractAttachmentData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ContractAttachmentDataRepository extends JpaRepository<ContractAttachmentData, Long> {

    Set<ContractAttachmentData> findByAttachmentIdIn(Set<Long> attachmentIds);

    void deleteByAttachmentId(Long attachmentId);
}
