import socket
import threading
import random
import string
import time

def generate_start_message():
    return ''.join(random.choices(string.ascii_letters + string.digits, k=16))

def send_data(gateway_ip, start_gateway_port, local_port, message, student):
    try:
        local_ip = socket.gethostbyname(socket.gethostname())
        udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        udp_socket.bind((local_ip, local_port))

        timeout = 2
        addr = None

        while addr == None:
            try:
                udp_socket.settimeout(timeout)
                udp_socket.sendto(message.encode(), (gateway_ip, start_gateway_port))
                response, addr = udp_socket.recvfrom(50000)
                break
            except Exception as e:
                pass

        new_gateway_port = addr[1]

        random_number = student
        udp_socket.sendto(str(random_number).encode(), (gateway_ip, new_gateway_port))
    finally:
        udp_socket.close()

def main():
    students_count = 200
    gateway_ip = '192.168.217.3'
    gateway_port = 5951

    start_time = time.time()
    threads = []

    for i in range(students_count):
        start_message = generate_start_message()
        local_port = 6000 + i
        t = threading.Thread(target=send_data, args=(gateway_ip, gateway_port, local_port, start_message, i + 1))
        threads.append(t)
        t.start()

    for t in threads:
        t.join()

    execution_time = round(time.time() - start_time, 2)
    print("Время выполнения:", execution_time, "сек")

if __name__ == "__main__":
    main()