import re
import csv

log_file = "logs.log"  
output_csv = "parsed_log_data.csv"

# Regular expression pattern
pattern = re.compile(
    r"(REST|gRPC) Communication - Time=(\d+\.\d+)ms, Size=(\d+)bytes, Attributes=(\d+), ArraySize=(\d+)"
)

# Open files and parse
with open(log_file, "r") as f, open(output_csv, "w", newline="") as out_csv:
    writer = csv.writer(out_csv)
    writer.writerow(["Type", "Time(ms)", "Size(bytes)", "Attributes", "ArraySize"])
    
    for line in f:
        match = pattern.search(line)
        if match:
            type_, time, size, attributes, array_size = match.groups()
            writer.writerow([type_, float(time), int(size), int(attributes), int(array_size)])

print(f"Parsing complete. Data saved to {output_csv}")
