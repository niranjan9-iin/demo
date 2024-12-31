Run main file HcsCompareApplication as java application. 
After server starts hit the below url:
To see differences from v1 and v3:
curl --location 'localhost:8080/getHotelDetails/ORDHA/ICON'
To see differences from v2 and v3:
curl --location 'localhost:8080/getHotelDetailsPost' \
--header 'Content-Type: application/json' \
--data '{
    "hotelCodeBrandCodeList": [
        {
            "hotelCode": "ATLBH",
            "brandCode": "ICON"
        }
    ]
}'
