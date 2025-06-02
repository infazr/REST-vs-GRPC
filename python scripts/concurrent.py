import re
import matplotlib.pyplot as plt
import numpy as np
from scipy import stats

# Function to parse log file and extract metrics
def parse_logs(file_path):
    rest_times = []
    rest_sizes = []
    grpc_times = []
    grpc_sizes = []
    
    # Regular expression to match log lines
    log_pattern = r'(\w+) Communication - Time=(\d+\.\d+)ms, Size=(\d+)bytes, Attributes=(\d+), ArraySize=(\d+)'
    
    with open(file_path, 'r') as file:
        for line in file:
            match = re.search(log_pattern, line)
            if match:
                protocol = match.group(1)
                time = float(match.group(2))
                size = int(match.group(3))
                
                if protocol == 'REST':
                    rest_times.append(time)
                    rest_sizes.append(size)
                elif protocol == 'gRPC':
                    grpc_times.append(time)
                    grpc_sizes.append(size)
    
    return rest_times, rest_sizes, grpc_times, grpc_sizes

# Function to perform statistical analysis
def analyze_metrics(rest_times, rest_sizes, grpc_times, grpc_sizes):
    print("=== Statistical Analysis ===")
    
    # Response Time Analysis
    print("\nResponse Time (ms):")
    print(f"REST - Mean: {np.mean(rest_times):.2f}, Std: {np.std(rest_times):.2f}")
    print(f"gRPC - Mean: {np.mean(grpc_times):.2f}, Std: {np.std(grpc_times):.2f}")
    
    # T-test for response times
    t_stat, p_value = stats.ttest_ind(rest_times, grpc_times)
    print(f"T-test (Response Time): t-statistic={t_stat:.2f}, p-value={p_value:.4f}")
    
    # Payload Size Analysis
    print("\nPayload Size (bytes):")
    print(f"REST - Mean: {np.mean(rest_sizes):.2f}, Std: {np.std(rest_sizes):.2f}")
    print(f"gRPC - Mean: {np.mean(grpc_sizes):.2f}, Std: {np.std(grpc_sizes):.2f}")
    
    # T-test for payload sizes
    t_stat, p_value = stats.ttest_ind(rest_sizes, grpc_sizes)
    print(f"T-test (Payload Size): t-statistic={t_stat:.2f}, p-value={p_value:.4f}")

# Function to create visualizations
def create_visualizations(rest_times, rest_sizes, grpc_times, grpc_sizes):
    # Generate request sequence (1 to 6 for each protocol)
    request_sequence = list(range(1, len(rest_times) + 1))
    
    # Boxplot for Response Times
    plt.figure(figsize=(10, 6))
    plt.boxplot([rest_times, grpc_times], labels=['REST', 'gRPC'])
    plt.title('Response Time Distribution: REST vs gRPC')
    plt.ylabel('Time (ms)')
    plt.grid(True)
    plt.savefig('response_time_boxplot.png')
    plt.close()
    
    # Boxplot for Payload Sizes
    plt.figure(figsize=(10, 6))
    plt.boxplot([rest_sizes, grpc_sizes], labels=['REST', 'gRPC'])
    plt.title('Payload Size Distribution: REST vs gRPC')
    plt.ylabel('Size (bytes)')
    plt.grid(True)
    plt.savefig('payload_size_boxplot.png')
    plt.close()
    
    # Line Chart 1: Response Time Over Request Sequence
    plt.figure(figsize=(10, 6))
    plt.plot(request_sequence, rest_times, label='REST', color='#1f77b4', marker='o')
    plt.plot(request_sequence, grpc_times, label='gRPC', color='#ff7f0e', marker='o')
    plt.title('Response Time Over Request Sequence: REST vs gRPC')
    plt.xlabel('Request Sequence')
    plt.ylabel('Response Time (ms)')
    plt.legend()
    plt.grid(True)
    plt.savefig('response_time_line_chart.png')
    plt.close()
    
    # Line Chart 2: Payload Size Over Request Sequence
    plt.figure(figsize=(10, 6))
    plt.plot(request_sequence, rest_sizes, label='REST', color='#2ca02c', marker='o')
    plt.plot(request_sequence, grpc_sizes, label='gRPC', color='#d62728', marker='o')
    plt.title('Payload Size Over Request Sequence: REST vs gRPC')
    plt.xlabel('Request Sequence')
    plt.ylabel('Payload Size (bytes)')
    plt.legend()
    plt.grid(True)
    plt.savefig('payload_size_line_chart.png')
    plt.close()

# Main execution
def main():
    file_path = 'concurrent_logs.log'
    
    # Parse logs
    rest_times, rest_sizes, grpc_times, grpc_sizes = parse_logs(file_path)
    
    # Perform analysis
    analyze_metrics(rest_times, rest_sizes, grpc_times, grpc_sizes)
    
    # Create visualizations
    create_visualizations(rest_times, rest_sizes, grpc_times, grpc_sizes)
    print("\nPlots generated: response_time_boxplot.png, payload_size_boxplot.png, response_time_line_chart.png, payload_size_line_chart.png")

if __name__ == "__main__":
    main()