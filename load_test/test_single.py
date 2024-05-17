import socket
import threading
import random
import string
import time

def generate_start_message():
    return ''.join(random.choices(string.ascii_letters + string.digits, k=16))

def send_data(gateway_ip, start_gateway_port, local_port, message, student):
    try:
        local_ip = '0.0.0.0'
        udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        udp_socket.bind((local_ip, local_port))

        timeout = 2
        addr = None

        while addr is None:
            try:
                udp_socket.settimeout(timeout)
                udp_socket.sendto(message.encode(), (gateway_ip, start_gateway_port))
                print(f"Sent message to {gateway_ip}:{start_gateway_port}")
                response, addr = udp_socket.recvfrom(100000)
                print(f"Received response from {addr}")
                break
            except socket.timeout:
                print(f"Timeout: no response received within {timeout} seconds")
            except Exception as e:
                print(f"Exception occurred: {e}")

        if addr:
            new_gateway_port = addr[1]
            random_number = student
            udp_socket.sendto(str(random_number).encode(), (gateway_ip, new_gateway_port))
            print(f"Sent student number to {gateway_ip}:{new_gateway_port}")
    finally:
        udp_socket.close()

def main():
    gateway_ip = '192.168.42.153'
    gateway_port = 5951

    start_time = time.time()

    start_message = generate_start_message()
    local_port = 6001
    id = 2
    send_data(gateway_ip, gateway_port, local_port, start_message, id)

    execution_time = round(time.time() - start_time, 2)
    print("Время выполнения:", execution_time, "сек")

if __name__ == "__main__":
    main()
