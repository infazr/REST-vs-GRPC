import requests
import concurrent.futures
import time

url = 'http://54.79.124.106:5080/api/data/grpc'
params = {'array': '5', 'attributes': '5'}

num_concurrent_requests = 20  # per second
duration_seconds = 10         # how many times to repeat

def send_request():
    try:
        response = requests.get(url, params=params, timeout=10)
        return response.status_code
    except Exception as e:
        return f"Error: {e}"

def send_batch():
    with concurrent.futures.ThreadPoolExecutor(max_workers=num_concurrent_requests) as executor:
        futures = [executor.submit(send_request) for _ in range(num_concurrent_requests)]
        results = [future.result() for future in concurrent.futures.as_completed(futures)]
        print(f"Batch results: {results}")

for i in range(duration_seconds):
    print(f"--- Sending batch {i+1} ---")
    send_batch()
    time.sleep(1)  # wait 1 second before the next batch
