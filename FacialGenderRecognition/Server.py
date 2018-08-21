from PIL import Image
import socket
import sys
import os
import io
import subprocess
import base64

# -*- coding: utf-8 -*-
import numpy as np
from keras.models import load_model
from keras.preprocessing import image

SERVER_HOST = '172.17.156.162' #this is your localhost
CLIENT_HOST = '172.17.132.71'
SERVER_PORT = 8080
CLIENT_PORT = 8880
TEXT_FILE   = 'package.txt'
IMAGE_FILE  = 'image.jpg'
MODEL_FILE  = 'Training/my_cnn.h5'

def prediction():
    gender_model = load_model(MODEL_FILE)
    test_image = image.load_img(IMAGE_FILE, target_size = (128, 128))
    test_image = image.img_to_array(test_image)
    test_image = np.expand_dims(test_image, axis = 0) # the third dimenssion is for batch
    result = gender_model.predict(test_image)
    #training_set.class_indices
    if result[0][0] == 1:
        prediction = 'This image is female.'
    else:
        prediction = 'This image is male.'
    return prediction


s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
#socket.socket: must use to create a socket.
#socket.AF_INET: Address Format, Internet = IP Addresses.
#socket.SOCK_STREAM: two-way, connection-based byte streams.
print ('socket created')
 
#Bind socket to Host and Port
try:
    s.bind((SERVER_HOST, SERVER_PORT))
except socket.error as err:
    print ('Bind Failed, Error Code: ' + str(err[0]) + ', Message: ' + err[1])
    sys.exit()

print ('Socket Bind Success!')
 
#listen(): This method sets up and start TCP listener.
s.listen(10)
print ('Socket is now listening')
 
while 1:
    sock, addr = s.accept()
    print ('Connect with ' + addr[0] + ': ' + str(addr[1]))
    f = open(TEXT_FILE,'w+')
    Image.new('RGB', (3120, 4160), color = 'white').save(IMAGE_FILE)
    connected = True 
    while connected:
        f = open(TEXT_FILE,'ab')
        data = sock.recv(26214400)
        if data:
            if ("[END]" in data.decode("utf-8")):
                f.write(data[:-7]) 
                print("receive the data from android")
                subprocess.call(['java', '-jar', 'Images.jar', TEXT_FILE, IMAGE_FILE])
                s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                s.connect((CLIENT_HOST, CLIENT_PORT))
                print("predict sex of the image")
                s.send(prediction().encode())
                os.remove(TEXT_FILE)
                s.close()
                print("wait for next request")
            elif ("[QUIT]" in data.decode("utf-8")):
                print(data)
                sock.close();
            else:
                f.write(data) 
        else:
            print ('disconnect with ' + addr[0] + ': ' + str(addr[1]))
            break
s.close()