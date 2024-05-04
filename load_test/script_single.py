import socket
import threading
import random
import string

def generate_start_message():
    return ''.join(random.choices(string.ascii_letters + string.digits, k=16))

def send_data(gateway_ip, start_gateway_port, message):
    try:
        local_ip = socket.gethostbyname(socket.gethostname())
        print(f"Local IP: {local_ip}")
        local_port = 5953
        udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        udp_socket.bind((local_ip, local_port))
        
        udp_socket.sendto(message.encode(), (gateway_ip, start_gateway_port))

        response, addr = udp_socket.recvfrom(50000)
        print(f"Received response from {addr}")
        
        new_gateway_port = addr[1]

        random_number = random.randint(1, 280)
        udp_socket.sendto(str(random_number).encode(), (gateway_ip, new_gateway_port))
        print(f"Sent random number to {gateway_ip}:{new_gateway_port}: {random_number}")
    except Exception as e:
        print(f"Error sending/receiving message: {e}")
    finally:
        udp_socket.close()

def main():
    gateway_ip = '192.168.100.6'
    gateway_port = 5951

    start_message = generate_start_message()
    send_data(gateway_ip, gateway_port, start_message)

if __name__ == "__main__":
    main()