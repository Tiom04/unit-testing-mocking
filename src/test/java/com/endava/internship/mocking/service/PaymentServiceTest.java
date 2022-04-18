package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.Payment;
import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import com.endava.internship.mocking.repository.PaymentRepository;
import com.endava.internship.mocking.repository.UserRepository;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class PaymentServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PaymentRepository paymentRepository;
    @Mock
    ValidationService validationService;

    PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(userRepository, paymentRepository, validationService);
    }

    @Test
    void createPayment_nonExistingUser_noSuchUserException() {
        User user = new User(1, "Tim", Status.ACTIVE);

        Mockito.doNothing().when(validationService).validateAmount(2.51);
        assertThrows(NoSuchElementException.class, () -> paymentService.createPayment(1, 2.51));

        Mockito.verify(validationService, Mockito.times(0)).validateUser(user);
        Mockito.verify(validationService).validateAmount(2.51);
    }

    @Test
    void createPayment_createdThenShouldValidateUserAndSavePayment() {
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of((new User(1, "Mock", Status.ACTIVE))));
        paymentService.createPayment(1, 2.51);
        Mockito.verify(validationService).validateUser(any(User.class));
        Mockito.verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void editMessage_shouldReturnPaymentClass() {
        Payment payment = new Payment(1, 2.2, "mes");
        Mockito.verify(validationService,Mockito.times(0)).validatePaymentId(any(UUID.class));
        Mockito.verify(validationService,Mockito.times(0)).validateMessage(any(String.class));

        Mockito.when(paymentRepository.editMessage(payment.getPaymentId(), payment.getMessage())).thenReturn(payment);
        assertEquals(Payment.class, (paymentRepository.editMessage(payment.getPaymentId(), "mes")).getClass());
    }

    @Test
    void getAllByAmountExceeding() {
        Mockito.when(paymentRepository.findAll()).thenReturn(new LinkedList<>());
        paymentService.getAllByAmountExceeding(1203.341);

        Mockito.verify(paymentRepository).findAll();
    }
}
