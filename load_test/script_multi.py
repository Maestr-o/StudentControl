import socket
import threading
import random
import string

def generate_start_message():
    return ''.join(random.choices(string.ascii_letters + string.digits, k=16))

def send_data(gateway_ip, start_gateway_port, local_port, message):
    try:
        local_ip = socket.gethostbyname(socket.gethostname())
        udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        udp_socket.bind((local_ip, local_port))
        
        udp_socket.sendto(message.encode(), (gateway_ip, start_gateway_port))
        
        response, addr = udp_socket.recvfrom(50000)
        print(f"Received response from {addr}")
        
        new_gateway_port = addr[1]

        random_number = random.randint(1, 290)
        udp_socket.sendto(str(random_number).encode(), (gateway_ip, new_gateway_port))
        print(f"Sent random number to {gateway_ip}:{new_gateway_port}: {random_number}")
    except Exception as e:
        print(f"Error sending/receiving message: {e}")
    finally:
        udp_socket.close()

def main():
    gateway_ip = '192.168.100.6'
    gateway_port = 5951
    
    threads = []
    for i in range(200):
        start_message = generate_start_message()
        local_port = 6000 + i
        t = threading.Thread(target=send_data, args=(gateway_ip, gateway_port, local_port, start_message))
        threads.append(t)
        t.start()
        
    for t in threads:
        t.join()

if __name__ == "__main__":
    main()