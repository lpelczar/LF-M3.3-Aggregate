package io.legacyfighter.cabs.repository;

import io.legacyfighter.cabs.entity.ContractAttachmentData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface ContractAttachmentDataRepository extends JpaRepository<ContractAttachmentData, Long> {

    Set<ContractAttachmentData> findByAttachmentNoIn(Set<UUID> attachmentNos);

    void deleteByAttachmentNo(UUID attachmentNo);
}
