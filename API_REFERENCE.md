1. Check Availability (GET with query params)

curl -X GET "http://localhost:9091/bus-ticketer-service/api/v1/reservation/availability?origin=A&destination=B&passenger_count=2&journey_date=2026-02-19"

2. Check Availability (POST with JSON body)

curl -X POST "http://localhost:9091/bus-ticketer-service/api/v1/reservation/availability" \
-H "Content-Type: application/json" \
-d '{
"origin": "A",
"destination": "B",
"passenger_count": 2,
"journey_date": "2026-02-19"
}'

3. Book Ticket (POST)

curl -X POST "http://localhost:9091/bus-ticketer-service/api/v1/reservation/book" \
-H "Content-Type: application/json" \
-d '{
"journey_id": 1,
"origin": "A",
"destination": "B",
"passenger_count": 2,
"contact_email": "john@example.com",
"payment_amount": 500.00,
"passengers": [
{
"name": "John Doe",
"phone": "9876543210",
"email": "john@example.com"
},
{
"name": "Jane Smith",
"phone": "9876543211",
"email": "jane@example.com"
}
]
}'


📋 API Parameters Reference

Parameter	    Values	        Notes
origin	        A, B, C, D	    Stop code
destination	    A, B, C, D	    Must differ from origin
passenger_count	Integer ≥ 1	    Number of passengers
journey_date	Date string	    e.g., "2026-02-19"
phone	        10 digits	    Required for each passenger
email	        Valid email	    Required

Tip: First run the availability check to get a journey_id, then use it for booking!

