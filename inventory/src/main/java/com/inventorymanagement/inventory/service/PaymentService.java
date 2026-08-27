package com.inventorymanagement.inventory.service;

import com.inventorymanagement.inventory.model.Invoice;
import com.inventorymanagement.inventory.model.InvoiceStatus;
import com.inventorymanagement.inventory.model.Payment;
import com.inventorymanagement.inventory.repository.InvoiceRepository;
import com.inventorymanagement.inventory.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            InvoiceRepository invoiceRepository) {

        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public Payment insertPayment(Payment payment) {

        // Get invoice ID
        Long invoiceId = payment.getInvoice().getId();

        // Check if invoice exists
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        payment.setInvoice(invoice);

        // Validate payment amount
        if (payment.getAmount() == null || payment.getAmount() <= 0) {
            throw new RuntimeException(
                    "Payment amount must be greater than zero"
            );
        }

        // Set payment date automatically if not provided
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        // Calculate new paid amount
        Double newPaidAmount =
                invoice.getPaidAmount() + payment.getAmount();

        // Prevent payment greater than invoice balance
        if (newPaidAmount > invoice.getTotalAmount()) {
            throw new RuntimeException(
                    "Payment amount exceeds the invoice balance"
            );
        }

        // Update invoice paid amount
        invoice.setPaidAmount(newPaidAmount);

        // Update invoice status
        if (Double.compare(newPaidAmount, 0.0) == 0) {

            invoice.setStatus(InvoiceStatus.UNPAID);

        } else if (newPaidAmount < invoice.getTotalAmount()) {

            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);

        } else {

            invoice.setStatus(InvoiceStatus.PAID);
        }

        // Save updated invoice
        invoiceRepository.save(invoice);

        // Save payment
        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Payment not found"));
    }
}