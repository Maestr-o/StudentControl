import socket
import threading
import random
import string
import time

local_ip = '0.0.0.0'
gateway_ip = '192.168.22.228'
start_gateway_port = 5951

def generate_start_message():
    return ''.join(random.choices(string.ascii_letters + string.digits, k=16))

def send_data(local_port, message, student):
    try:
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

def single_test():
    local_port = 6001
    id = random.randint(1, 200)
    start_message = generate_start_message()
    send_data(local_port, start_message, id)

def interrupt_multi_test():
    threads = []
    x = 1
    for y in range(50):
        for i in range(4):
            start_message = generate_start_message()
            local_port = 6001 + x
            x += 1
            id = (y - 1) * 4 + i
            t = threading.Thread(target=send_data, args=(local_port, start_message, id))
            threads.append(t)
            t.start()
        time.sleep(1)
    for t in threads:
        t.join()

def straight_multi_test():
    students_count = 200
    threads = []
    for i in range(students_count):
        start_message = generate_start_message()
        local_port = 6000 + i
        t = threading.Thread(target=send_data, args=(local_port, start_message, i + 1))
        threads.append(t)
        t.start()
    for t in threads:
        t.join()

def main():
    start_time = time.time()

    single_test()
    # interrupt_multi_test()
    # straight_multi_test()

    execution_time = round(time.time() - start_time, 2)
    print("Время выполнения:", execution_time, "сек")

if __name__ == "__main__":
    main()
