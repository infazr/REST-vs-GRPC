import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Load the CSV file
df = pd.read_csv('parsed_log_data.csv')

# Split the data by protocol
rest_df = df[df['Type'] == 'REST']
grpc_df = df[df['Type'] == 'gRPC']

# Ensure sorted by ArraySize for accurate line plots
rest_df = rest_df.sort_values('ArraySize')
grpc_df = grpc_df.sort_values('ArraySize')

# 1. Line Chart: Time vs Array Size
plt.figure(figsize=(10, 6))
sns.lineplot(data=rest_df, x='ArraySize', y='Time(ms)', label='REST')
sns.lineplot(data=grpc_df, x='ArraySize', y='Time(ms)', label='gRPC')
plt.title('Time vs Array Size')
plt.ylabel('Time (ms)')
plt.xlabel('Array Size')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.show()

# 2. Line Chart: Size(bytes) vs Time(ms)
plt.figure(figsize=(10, 6))
sns.lineplot(data=rest_df, x='Size(bytes)', y='Time(ms)', label='REST')
sns.lineplot(data=grpc_df, x='Size(bytes)', y='Time(ms)', label='gRPC')
plt.title('Time vs Payload Size (bytes)')
plt.ylabel('Time (ms)')
plt.xlabel('Size (bytes)')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.show()

# 3. Scatter Plot: REST Time vs gRPC Time
merged_df = pd.merge(rest_df[['ArraySize', 'Time(ms)']], grpc_df[['ArraySize', 'Time(ms)']],
                     on='ArraySize', suffixes=('_REST', '_gRPC'))

plt.figure(figsize=(8, 6))
sns.scatterplot(data=merged_df, x='Time(ms)_REST', y='Time(ms)_gRPC')
plt.plot([merged_df['Time(ms)_REST'].min(), merged_df['Time(ms)_REST'].max()],
         [merged_df['Time(ms)_REST'].min(), merged_df['Time(ms)_REST'].max()],
         color='red', linestyle='--', label='Equal Performance')
plt.title('REST vs gRPC Time')
plt.xlabel('REST Time (ms)')
plt.ylabel('gRPC Time (ms)')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.show()

# 4. Bar Chart: Average Time per Protocol
avg_times = df.groupby('Type')['Time(ms)'].mean().reset_index()

plt.figure(figsize=(6, 5))
sns.barplot(data=avg_times, x='Type', y='Time(ms)', palette='muted')
plt.title('Average Response Time per Protocol')
plt.ylabel('Average Time (ms)')
plt.tight_layout()
plt.show()

# 5. Stacked Area Chart: Cumulative Time over Array Size
rest_df['CumulativeTime'] = rest_df['Time(ms)'].cumsum()
grpc_df['CumulativeTime'] = grpc_df['Time(ms)'].cumsum()

plt.figure(figsize=(10, 6))
plt.fill_between(rest_df['ArraySize'], rest_df['CumulativeTime'], alpha=0.5, label='REST')
plt.fill_between(grpc_df['ArraySize'], grpc_df['CumulativeTime'], alpha=0.5, label='gRPC')
plt.title('Cumulative Time Over Increasing Array Size')
plt.xlabel('Array Size')
plt.ylabel('Cumulative Time (ms)')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.show()
