import requests

# Base URL
base_url = "http://54.79.124.106:5080/api/data/grpc"

# Initial values
attributes = 5
array = 5

# Loop to send 50 requests
for i in range(50):
    params = {
        "attributes": attributes,
        "array": array
    }

    print(f"Sending request {i+1}: attributes={attributes}, array={array}")
    
    try:
        response = requests.get(base_url, params=params)
        print(f"Status Code: {response.status_code}")
        print(f"Response: {response.text[:100]}...\n")  # Print first 100 chars
    except Exception as e:
        print(f"Request failed: {e}")

    # Increment values
    attributes += 5
    array += 5
