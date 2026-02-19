package com.busticket.domain.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for booking/reservation confirmation
 */
public class ReservationResponse {
    private int reservationId;
    private String ticketNumber;
    private String bookingNumber;
    private JourneyDetail journeyInfo;
    private List<BookingDetail> bookings;
    private BigDecimal totalPrice;
    private String paymentStatus;
    private boolean confirmationEmailSent;
    private String confirmationSentTo;
    private LocalDateTime bookingDate;

    public ReservationResponse() {
    }

    public ReservationResponse(int reservationId, String ticketNumber, String bookingNumber,
                              JourneyDetail journeyInfo, List<BookingDetail> bookings,
                              BigDecimal totalPrice, String paymentStatus, boolean confirmationEmailSent,
                              String confirmationSentTo, LocalDateTime bookingDate) {
        this.reservationId = reservationId;
        this.ticketNumber = ticketNumber;
        this.bookingNumber = bookingNumber;
        this.journeyInfo = journeyInfo;
        this.bookings = bookings;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.confirmationEmailSent = confirmationEmailSent;
        this.confirmationSentTo = confirmationSentTo;
        this.bookingDate = bookingDate;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public JourneyDetail getJourneyInfo() {
        return journeyInfo;
    }

    public void setJourneyInfo(JourneyDetail journeyInfo) {
        this.journeyInfo = journeyInfo;
    }

    public List<BookingDetail> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingDetail> bookings) {
        this.bookings = bookings;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public boolean isConfirmationEmailSent() {
        return confirmationEmailSent;
    }

    public void setConfirmationEmailSent(boolean confirmationEmailSent) {
        this.confirmationEmailSent = confirmationEmailSent;
    }

    public String getConfirmationSentTo() {
        return confirmationSentTo;
    }

    public void setConfirmationSentTo(String confirmationSentTo) {
        this.confirmationSentTo = confirmationSentTo;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    /**
     * Journey detail information
     */
    public static class JourneyDetail {
        private int journeyId;
        private int busId;
        private String journeyNumber;
        private String origin;
        private String destination;
        private LocalDateTime departureTime;
        private LocalDateTime arrivalTime;
        private String direction;

        public JourneyDetail() {
        }

        public JourneyDetail(int journeyId, int busId, String journeyNumber, String origin,
                            String destination, LocalDateTime departureTime, LocalDateTime arrivalTime, String direction) {
            this.journeyId = journeyId;
            this.busId = busId;
            this.journeyNumber = journeyNumber;
            this.origin = origin;
            this.destination = destination;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.direction = direction;
        }

        public int getJourneyId() {
            return journeyId;
        }

        public void setJourneyId(int journeyId) {
            this.journeyId = journeyId;
        }

        public int getBusId() {
            return busId;
        }

        public void setBusId(int busId) {
            this.busId = busId;
        }

        public String getJourneyNumber() {
            return journeyNumber;
        }

        public void setJourneyNumber(String journeyNumber) {
            this.journeyNumber = journeyNumber;
        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public LocalDateTime getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(LocalDateTime departureTime) {
            this.departureTime = departureTime;
        }

        public LocalDateTime getArrivalTime() {
            return arrivalTime;
        }

        public void setArrivalTime(LocalDateTime arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        @Override
        public String toString() {
            return "JourneyDetail{" +
                    "journeyId=" + journeyId +
                    ", busId=" + busId +
                    ", journeyNumber='" + journeyNumber + '\'' +
                    ", origin='" + origin + '\'' +
                    ", destination='" + destination + '\'' +
                    ", departureTime=" + departureTime +
                    ", arrivalTime=" + arrivalTime +
                    ", direction='" + direction + '\'' +
                    '}';
        }
    }

    /**
     * Individual booking detail
     */
    public static class BookingDetail {
        private int bookingId;
        private String ticketNumber;
        private String passengerName;
        private String passengerPhone;
        private String passengerEmail;
        private String seatId;
        private BigDecimal fare;
        private String status;

        public BookingDetail() {
        }

        public BookingDetail(int bookingId, String ticketNumber, String passengerName, String passengerPhone,
                            String passengerEmail, String seatId, BigDecimal fare, String status) {
            this.bookingId = bookingId;
            this.ticketNumber = ticketNumber;
            this.passengerName = passengerName;
            this.passengerPhone = passengerPhone;
            this.passengerEmail = passengerEmail;
            this.seatId = seatId;
            this.fare = fare;
            this.status = status;
        }

        public int getBookingId() {
            return bookingId;
        }

        public void setBookingId(int bookingId) {
            this.bookingId = bookingId;
        }

        public String getTicketNumber() {
            return ticketNumber;
        }

        public void setTicketNumber(String ticketNumber) {
            this.ticketNumber = ticketNumber;
        }

        public String getPassengerName() {
            return passengerName;
        }

        public void setPassengerName(String passengerName) {
            this.passengerName = passengerName;
        }

        public String getPassengerPhone() {
            return passengerPhone;
        }

        public void setPassengerPhone(String passengerPhone) {
            this.passengerPhone = passengerPhone;
        }

        public String getPassengerEmail() {
            return passengerEmail;
        }

        public void setPassengerEmail(String passengerEmail) {
            this.passengerEmail = passengerEmail;
        }

        public String getSeatId() {
            return seatId;
        }

        public void setSeatId(String seatId) {
            this.seatId = seatId;
        }

        public BigDecimal getFare() {
            return fare;
        }

        public void setFare(BigDecimal fare) {
            this.fare = fare;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "BookingDetail{" +
                    "bookingId=" + bookingId +
                    ", ticketNumber='" + ticketNumber + '\'' +
                    ", passengerName='" + passengerName + '\'' +
                    ", passengerPhone='" + passengerPhone + '\'' +
                    ", passengerEmail='" + passengerEmail + '\'' +
                    ", seatId='" + seatId + '\'' +
                    ", fare=" + fare +
                    ", status='" + status + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ReservationResponse{" +
                "reservationId=" + reservationId +
                ", ticketNumber='" + ticketNumber + '\'' +
                ", bookingNumber='" + bookingNumber + '\'' +
                ", journeyInfo=" + journeyInfo +
                ", bookings=" + bookings +
                ", totalPrice=" + totalPrice +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", confirmationEmailSent=" + confirmationEmailSent +
                ", confirmationSentTo='" + confirmationSentTo + '\'' +
                ", bookingDate=" + bookingDate +
                '}';
    }
}
