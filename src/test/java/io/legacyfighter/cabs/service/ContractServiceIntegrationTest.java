package io.legacyfighter.cabs.service;

import io.legacyfighter.cabs.dto.ContractAttachmentDTO;
import io.legacyfighter.cabs.dto.ContractDTO;
import io.legacyfighter.cabs.entity.Contract;
import io.legacyfighter.cabs.entity.ContractAttachment;
import io.legacyfighter.cabs.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContractServiceIntegrationTest {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    ContractService contractService;

    @BeforeEach
    void setup() {
        contractRepository.deleteAll();
    }

    @Test
    void shouldCreateContract() {
        //given
        ContractDTO contractDTO = aContractDto("partner", "subject");

        //when
        Contract contract = contractService.createContract(contractDTO);

        //then
        ContractDTO result = contractService.findDto(contract.getId());
        assertEquals("partner", result.getPartnerName());
        assertEquals("subject", result.getSubject());
        assertEquals("C/1/partner", result.getContractNo());
    }

    @Test
    @Transactional
    void shouldFindContract() {
        //given
        ContractDTO contractDTO = aContractDto("partner", "subject");

        //when
        Contract contract = contractService.createContract(contractDTO);

        //then
        Contract result = contractService.find(contract.getId());
        assertEquals("partner", result.getPartnerName());
    }

    @Test
    void shouldBeAbleToProposeAttachment() {
        //given
        ContractDTO contractDTO = aContractDto("partner", "subject");
        //and
        Contract contract = contractService.createContract(contractDTO);
        //and
        ContractAttachmentDTO contractAttachmentDTO = aContractAttachmentDto();

        //when
        contractService.proposeAttachment(contract.getId(), contractAttachmentDTO);

        //then
        ContractDTO contractResult = contractService.findDto(contract.getId());
        assertEquals(1, contractResult.getAttachments().size());
        ContractAttachmentDTO attachmentResult = contractResult.getAttachments().get(0);
        assertEquals(contractResult.getId(), attachmentResult.getContractId());
        assertTrue(Arrays.equals(new byte[]{9}, attachmentResult.getData()));
    }

    @Test
    void shouldBeAbleToAcceptAttachment() {
        //given
        ContractDTO contractDTO = aContractDto("partner", "subject");
        //and
        Contract contract = contractService.createContract(contractDTO);
        //and
        ContractAttachmentDTO contractAttachmentDTO = aContractAttachmentDto();
        //and
        ContractAttachmentDTO attachment = contractService.proposeAttachment(contract.getId(), contractAttachmentDTO);

        //when
        contractService.acceptAttachment(attachment.getId());

        //then
        ContractDTO contractResult = contractService.findDto(contract.getId());
        ContractAttachmentDTO attachmentResult = contractResult.getAttachments().get(0);
        assertEquals(ContractAttachment.Status.ACCEPTED_BY_ONE_SIDE, attachmentResult.getStatus());
    }

    @Test
    void shouldBeAbleToAcceptAttachmentByBothSides() {
        //given
        ContractDTO contractDTO = aContractDto("partner", "subject");
        //and
        Contract contract = contractService.createContract(contractDTO);
        //and
        ContractAttachmentDTO contractAttachmentDTO = aContractAttachmentDto();
        //and
        ContractAttachmentDTO attachment = contractService.proposeAttachment(contract.getId(), contractAttachmentDTO);

        //when
        contractService.acceptAttachment(attachment.getId());
        //and
        contractService.acceptAttachment(attachment.getId());

        //then
        ContractDTO contractResult = contractService.findDto(contract.getId());
        ContractAttachmentDTO attachmentResult = contractResult.getAttachments().get(0);
        assertEquals(ContractAttachment.Status.ACCEPTED_BY_BOTH_SIDES, attachmentResult.getStatus());
    }

    @Test
    void shouldBeAbleToRejectAttachment() {
        //given
        ContractDTO contractDTO = aContractDto("partner", "subject");
        //and
        Contract contract = contractService.createContract(contractDTO);
        //and
        ContractAttachmentDTO contractAttachmentDTO = aContractAttachmentDto();
        //and
        ContractAttachmentDTO attachment = contractService.proposeAttachment(contract.getId(), contractAttachmentDTO);

        //when
        contractService.rejectAttachment(attachment.getId());

        //then
        ContractDTO contractResult = contractService.findDto(contract.getId());
        ContractAttachmentDTO attachmentResult = contractResult.getAttachments().get(0);
        assertEquals(ContractAttachment.Status.REJECTED, attachmentResult.getStatus());
    }

    @Test
    void shouldBeAbleToRemoveAttachment() {
        //given
        ContractDTO contractDTO = aContractDto("partner", "subject");
        //and
        Contract contract = contractService.createContract(contractDTO);
        //and
        ContractAttachmentDTO contractAttachmentDTO = aContractAttachmentDto();
        //and
        ContractAttachmentDTO attachment = contractService.proposeAttachment(contract.getId(), contractAttachmentDTO);

        //when
        contractService.removeAttachment(contract.getId(), attachment.getId());

        //then
        ContractDTO contractResult = contractService.findDto(contract.getId());
        assertEquals(0, contractResult.getAttachments().size());
    }

    @Test
    void shouldBeAbleToAcceptContractWithAllAttachmentsAcceptedByBothSides() {
        //given
        ContractDTO contractDTO = aContractDto("partner", "subject");
        //and
        Contract contract = contractService.createContract(contractDTO);
        //and
        ContractAttachmentDTO contractAttachmentDTO = aContractAttachmentDto();
        //and
        ContractAttachmentDTO attachment = contractService.proposeAttachment(contract.getId(), contractAttachmentDTO);

        //and
        contractService.acceptAttachment(attachment.getId());
        contractService.acceptAttachment(attachment.getId());

        //when
        contractService.acceptContract(contract.getId());

        //then
        ContractDTO contractResult = contractService.findDto(contract.getId());
        assertEquals(Contract.Status.ACCEPTED, contractResult.getStatus());
    }

    @Test
    void shouldBeAbleToAcceptContractWithNoAttachments() {
        //given
        ContractDTO contractDTO = aContractDto("partner", "subject");
        //and
        Contract contract = contractService.createContract(contractDTO);

        //when
        contractService.acceptContract(contract.getId());

        //then
        ContractDTO contractResult = contractService.findDto(contract.getId());
        assertEquals(Contract.Status.ACCEPTED, contractResult.getStatus());
    }

    @Test
    void shouldFailToAcceptContractWithAttachmentAcceptedOnlyByOneSide() {
        //given
        ContractDTO contractDTO = aContractDto("partner", "subject");
        //and
        Contract contract = contractService.createContract(contractDTO);
        //and
        ContractAttachmentDTO contractAttachmentDTO = aContractAttachmentDto();
        //and
        ContractAttachmentDTO attachment = contractService.proposeAttachment(contract.getId(), contractAttachmentDTO);
        //and
        contractService.acceptAttachment(attachment.getId());

        //expect
        assertThrows(IllegalStateException.class, () -> contractService.acceptContract(contract.getId()));
    }

    @Test
    void shouldBeAbleToRejectContract() {
        //given
        ContractDTO contractDTO = aContractDto("partner", "subject");
        //and
        Contract contract = contractService.createContract(contractDTO);
        //and
        ContractAttachmentDTO contractAttachmentDTO = aContractAttachmentDto();
        //and
        ContractAttachmentDTO attachment = contractService.proposeAttachment(contract.getId(), contractAttachmentDTO);

        //and
        contractService.acceptAttachment(attachment.getId());

        //when
        contractService.rejectContract(contract.getId());

        //then
        ContractDTO contractResult = contractService.findDto(contract.getId());
        assertEquals(Contract.Status.REJECTED, contractResult.getStatus());
    }

    @Test
    void shouldBeAbleToRejectContractWithoutAttachments() {
        //given
        ContractDTO contractDTO = aContractDto("partner", "subject");
        //and
        Contract contract = contractService.createContract(contractDTO);

        //when
        contractService.rejectContract(contract.getId());

        //then
        ContractDTO contractResult = contractService.findDto(contract.getId());
        assertEquals(Contract.Status.REJECTED, contractResult.getStatus());
    }

    private ContractAttachmentDTO aContractAttachmentDto() {
        ContractAttachmentDTO contractAttachmentDTO = new ContractAttachmentDTO();
        contractAttachmentDTO.setData(new byte[]{9});
        return contractAttachmentDTO;
    }

    private ContractDTO aContractDto(String partnerName, String subject) {
        ContractDTO contractDTO = new ContractDTO();
        contractDTO.setPartnerName(partnerName);
        contractDTO.setSubject(subject);
        return contractDTO;
    }

}