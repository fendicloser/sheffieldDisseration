# -*- coding: utf-8 -*-
"""
Created on Sun Jun 24 21:53:56 2018

@author: pi
"""


import RPi.GPIO as GPIO
import socket
import random
import time
import threading
import pigpio
import DHT22
import datetime
import pymysql

import smtplib

_charset = 'utf-8'
_delim = '#'


a=20    #car front-left wheel 
b=21    #car front-right wheel
c=19    #car back-left wheel
d=26    #car back-right wheel
THPin=2 #pin for AM2302 temperature and humditiy
gasPin=8 #pin for MQ-2 gas sensor

tpir=14
led=15

user='fendicloser@gmail.com'
password='vjtzisqrywdovqlh'
subject='There is a living creature approaching your device!!!'
toAdd='1033285894@qq.com'
fromAdd=user
header='To: '+toAdd+'\n'+'From: '+ fromAdd+'\n'+'subject: '+subject
body='if it is not your families or your pet, please pay attention to the security of your monitoring device, someone may be vandalizing, moving, or stealing its.'



#obstacle avoidance movemnt
EchoPin=0 #recieve port 
TrigPin=1 #trigger port 
frontServoPin=23#servo motor

#AM2302
pi=pigpio.pi()
dht22=DHT22.sensor(pi,THPin)

#camera steerting servo,initialization to 90 degree
ServoUpDownPin=9
ServoLeftRightPin=11
ServoLeftRightPos=90
ServoUpDownPos=90

#tracking movement sensor, for 4 infrared sensor
TrackLeftPin1=3
TrackLeftPin2=5
TrackRightPin1=4
TrackRightPin2=18

GPIO.setmode(GPIO.BCM)  #setting the work mode of GPIO, which is BCM
#setting the work mode for each pin (out/in)
#the work mode of servo motor is PWM
GPIO.setup(a,GPIO.OUT)
GPIO.setup(b,GPIO.OUT)
GPIO.setup(c,GPIO.OUT)
GPIO.setup(d,GPIO.OUT)
global pwm_UpDownServo
global pwm_LeftRightServo
GPIO.setup(ServoUpDownPin,GPIO.OUT)
GPIO.setup(ServoLeftRightPin,GPIO.OUT)
GPIO.setup(gasPin,GPIO.IN)
GPIO.setup(EchoPin,GPIO.IN)
GPIO.setup(TrigPin,GPIO.OUT)
GPIO.setup(frontServoPin,GPIO.OUT)

GPIO.setup(TrackLeftPin1,GPIO.IN)
GPIO.setup(TrackLeftPin2,GPIO.IN)
GPIO.setup(TrackRightPin1,GPIO.IN)
GPIO.setup(TrackRightPin2,GPIO.IN)

GPIO.setup(tpir,GPIO.IN)
GPIO.setup(led,GPIO.OUT)


pwm_UpDownServo=GPIO.PWM(ServoUpDownPin,50)
pwm_LeftRightServo=GPIO.PWM(ServoLeftRightPin,50)
pwm_servoPin=GPIO.PWM(frontServoPin,50)
pwm_UpDownServo.start(0)
pwm_LeftRightServo.start(0)
pwm_servoPin.start(0)

#car modle go forward
def forward():
    GPIO.output(a,GPIO.HIGH)
    GPIO.output(b,GPIO.LOW)
    GPIO.output(c,GPIO.HIGH)
    GPIO.output(d,GPIO.LOW)
#car go back   
def back():
    GPIO.output(a,GPIO.LOW)
    GPIO.output(b,GPIO.HIGH)
    GPIO.output(c,GPIO.LOW)
    GPIO.output(d,GPIO.HIGH)
#car stop
def carstop():
    GPIO.output(a,GPIO.LOW)
    GPIO.output(b,GPIO.LOW)
    GPIO.output(c,GPIO.LOW)
    GPIO.output(d,GPIO.LOW)
#car turn left,low efficiency
def left():
    GPIO.output(a,GPIO.LOW)
    GPIO.output(b,GPIO.LOW)
    GPIO.output(c,GPIO.HIGH)
    GPIO.output(d,GPIO.LOW)
#car turn right,low efficiency
def right():
    GPIO.output(a,GPIO.HIGH)
    GPIO.output(b,GPIO.LOW)
    GPIO.output(c,GPIO.LOW)
    GPIO.output(d,GPIO.LOW)
#car turn left in place,
#the right wheel turns clockwise
#the left wheel turns counterclockwise
def spin_left():
    GPIO.output(a,GPIO.LOW)
    GPIO.output(b,GPIO.HIGH)
    GPIO.output(c,GPIO.HIGH)
    GPIO.output(d,GPIO.LOW)
#car turn right in place,
#the left wheel turns clockwise
#the right wheel turns counterclockwise
def spin_right():
    GPIO.output(a,GPIO.HIGH)
    GPIO.output(b,GPIO.LOW)
    GPIO.output(c,GPIO.LOW)
    GPIO.output(d,GPIO.HIGH)

#horizontal forward
def servo_up():
    pwm_LeftRightServo.ChangeDutyCycle(7)
    time.sleep(1)
    pwm_LeftRightServo.ChangeDutyCycle(0)
#the direction of camera doesn't need much vertical vision.    
def servo_Upwatch():
    pwm_UpDownServo.ChangeDutyCycle(3)
    time.sleep(0.04)
    pwm_UpDownServo.ChangeDutyCycle(2.5)
    time.sleep(0.04)
    pwm_UpDownServo.ChangeDutyCycle(2)
    time.sleep(0.04)
    pwm_UpDownServo.ChangeDutyCycle(1.8)
    time.sleep(0.04)
    pwm_UpDownServo.ChangeDutyCycle(1.5)
    time.sleep(0.04)
    pwm_UpDownServo.ChangeDutyCycle(1.3)
    time.sleep(0.04)     
    pwm_UpDownServo.ChangeDutyCycle(1)
    time.sleep(1)
    pwm_UpDownServo.ChangeDutyCycle(1.5)
    time.sleep(0.5)
    pwm_UpDownServo.ChangeDutyCycle(2.5)
    time.sleep(0.5)
    pwm_UpDownServo.ChangeDutyCycle(3.2)
    time.sleep(1)
    pwm_UpDownServo.ChangeDutyCycle(0)
#left-right servo motor from right to left
def servo_left():
    for i in range(180,0,-10):
        pwm_LeftRightServo.ChangeDutyCycle(2.5+10*i/180)
        time.sleep(0.04)  
#left-right servo motor from left to right
def servo_right():
     for i in range(0,180,10):
        pwm_LeftRightServo.ChangeDutyCycle(2.5+10*i/180)
        time.sleep(0.04)
    
#avoid servo motor's pulse affecting other sensors's result
def servo_stop():
     pwm_LeftRightServo.ChangeDutyCycle(0)
     pwm_UpDownServo.ChangeDutyCycle(0)
        
#ultrasonic measure the distance
def Distance(): 
    GPIO.output(TrigPin,GPIO.HIGH)
    #send unltrasonic wave by trig pin
    time.sleep(0.000015)
    GPIO.output(TrigPin,GPIO.LOW)
    #echo waiting for the reflected wave
    while not GPIO.input(EchoPin):
        pass
    t1=time.time()
    while GPIO.input(EchoPin):
        pass
    t2=time.time()
    #recrode duration and get distance by time and speed of sound
    time_value=t2-t1
    distance=time_value*340/2*100
    return distance

#check the left side of the car for obstacsle
def test_left():
    leftdistance=0
    pwm_servoPin.ChangeDutyCycle(13)
    time.sleep(0.5)
    pwm_servoPin.ChangeDutyCycle(0)
    leftdistance=Distance()
    return leftdistance
#check the front of the car ofr obstacles
def test_front():
    frontdistance=0
    pwm_servoPin.ChangeDutyCycle(7)
    time.sleep(0.5)
    pwm_servoPin.ChangeDutyCycle(0)
    frontdistance=Distance()
    return frontdistance

#socket encode data, send to client   
def send(sock, data):
    global sendLock
    sendLock.acquire()
    # print("send %s" % data)
    sock.send((data + _delim).encode(_charset))
    sendLock.release()

#get humidity and temperature by adafruit
def getTemperature(sock):
    dht22.trigger()
    time.sleep(0.5)
    y1=dht22.humidity()
    x1=dht22.temperature()
    return (x1,y1)

#ult
def Diistance():
    GPIO.output(TrigPin,GPIO.HIGH)
    time.sleep(0.000015)
    GPIO.output(TrigPin,GPIO.LOW)
    while not GPIO.input(EchoPin):
        pass
    t1=time.time()
    while GPIO.input(EchoPin):
        pass
    t2=time.time()
    #record ult-wave send time and the recieve time,get a duration
    time_value=t2-t1
    #get the distance by this duration and the speed of sound
    distance=time_value*340/2*100
    return distance
def turn_right():  
    pwm_servoPin.ChangeDutyCycle(4.5)
    time.sleep(0.5)
    pwm_servoPin.ChangeDutyCycle(0)
def turn_front():
    pwm_servoPin.ChangeDutyCycle(7)
    time.sleep(0.5)
    pwm_servoPin.ChangeDutyCycle(0)
def turn_left():
    pwm_servoPin.ChangeDutyCycle(13)
    time.sleep(0.5)
    pwm_servoPin.ChangeDutyCycle(0)
    

#obstacle avoidance movement  
def function5(sock):
    #first servo motor turn to front, test distance
    turn_front()
    time.sleep(0.5)
    distance=Diistance()
    print(distance)
    #if >100,go forward 3sec
    if distance>100:
        forward()
        time.sleep(3)
        carstop()
    #if >40 and <100,go forward 2sec
    elif distance>40 and distance<100:
        forward()
        time.sleep(2)
        carstop()
    #if <40 test left and right once
    elif distance<40:
        turn_left()
        time.sleep(0.5)
        distance_left=Diistance()
        print("car left distance:")
        print(distance_left)
        
        turn_right()
        time.sleep(0.5)
        distance_right=Diistance()
    
        print("car right distance:")
        print(distance_right)
        #if distance_left and distance_left <40 ,no way ,turn away
        if distance_right<40 and distance_left<40:
            spin_right()
            time.sleep(3)
            carstop()
        #find a better direction
        elif distance_right-distance_left>0:
            right()
            time.sleep(1.5)
            carstop()
        elif distance_right-distance_left<0:
            left()
            time.sleep(1.5)
            carstop()
#presetting path
#the speed of the car model 
#has been record accurately
def function6(sock):
    forward()
    time.sleep(4.1)
    left()
    time.sleep(7)
    forward()
    time.sleep(4.1)
    right()
    time.sleep(7)
    forward()
    time.sleep(4.1)
    right()
    time.sleep(7)
    forward()
    time.sleep(4.1)
    left()
    time.sleep(7)    
    #data = random.randint(10000, 99999)
    #print(data)

    #time.sleep(1)

#@ture is not sensing the tape@
#@false is sensing the tape@
#the condition of @if@ has been range of right to left
def function4(sock):
    TrackLeftValue1=GPIO.input(TrackLeftPin1)
    TrackLeftValue2=GPIO.input(TrackLeftPin2)
    TrackRightValue1=GPIO.input(TrackRightPin1)
    TrackRightValue2=GPIO.input(TrackRightPin2)
    
    if TrackLeftValue1==True and TrackLeftValue2==False and TrackRightValue2==False and TrackRightValue1==True:
        forward()
        time.sleep(0.08)
    elif TrackLeftValue1==False and TrackLeftValue2==False and TrackRightValue1==True and TrackRightValue2==False:
        right()
        time.sleep(0.08)
    elif TrackLeftValue1==True and TrackLeftValue2==False and TrackRightValue1==False and TrackRightValue2==False:
        left()
        time.sleep(0.08)
    elif TrackLeftValue1==True and TrackLeftValue2==True and TrackRightValue1==True and TrackRightValue2==False:
        spin_left()
        time.sleep(0.08)
    
    elif TrackLeftValue1==True and TrackLeftValue2==False and TrackRightValue1==True and TrackRightValue2==True:
        spin_right()
        time.sleep(0.08)
    elif TrackLeftValue1==False and TrackLeftValue2==True and TrackRightValue1==True and TrackRightValue2==True:
        right()
        time.sleep(0.08)
    
    elif TrackLeftValue1==True and TrackLeftValue2==True and TrackRightValue1==False and TrackRightValue2==True:
        left()
        time.sleep(0.08)
        
           
    elif TrackLeftValue1==True and TrackLeftValue2==True and TrackRightValue1==True and TrackRightValue2==True:
        carstop()
    elif TrackLeftValue1==False and TrackLeftValue2==False and TrackRightValue1==False and TrackRightValue2==False:
        carstop()
   

#encapsulate the recorded temperature and humidity,each second send once
def functionxy(sock):
    x,y = getTemperature(sock)
    data = "x:%.2f,y:%.2f" % (x, y)
    send(sock, data) 
    time.sleep(1)
#def getTemperature(sock):
#    dht22.trigger()
#    y1=dht22.humidity()
#    x1=dht22.temperature()
#    return (x1,y1)
def function7(sock):
    # select * from TemAndHumTest where temperature!='-999' order by date desc limit 10;
    dbcheck=pymysql.connect("localhost","root","aptx4869","sensordb")
    cursor=dbcheck.cursor()
    sendsql="select * from TemAndHumTest where temperature!='-999' order by date desc limit 10;"
    cursor.execute(sendsql)
    sendresults=cursor.fetchall()
    sendlist=[]
    for row in sendresults:
        #if row[1]!="-999" and row[2]!="-999":
        timed=row[0]
        te=row[1]
        hu=row[2]
        sendlist.append(timed)
        sendlist.append(te)
        sendlist.append(hu)
    data='#'.join(sendlist)
    print(data)
    send(sock,data)
    time.sleep(3)
    #the type of data is string,each element is isolated by '#'
    #but infront of first element and the back of last element no #
#main thread 
def waringfunction(sock):
    #first thing is monitor the gas
    gasStatue=GPIO.input(gasPin)
    data=0
    #if level changed, harmful gas is found
    if gasStatue==True:
        data=1
    elif gasStatue==False:
        data=25    
    if data == 25:
        #send(sock, 'z')
        send(sock,'z')
    time.sleep(1)
    GPIO.output(led,GPIO.LOW)
    if GPIO.input(tpir):
        print("pir detected")
        GPIO.output(led,GPIO.HIGH)
        
        s=smtplib.SMTP('smtp.gmail.com',587)
        s.ehlo()
        s.starttls()
        s.ehlo()
        s.login(user,password)
        s.sendmail(fromAdd,toAdd,header+'\n\n'+body)
        s.quit()
        
    #MySQL connection
    db=pymysql.connect("localhost","root","aptx4869","sensordb")
    #set a cursor    
    cursor=db.cursor()
    #sql sentense, "selection" operation
    checkIfEmpty="select * from TemAndHumTest limit1 order by date desc;"
    cursor.execute(checkIfEmpty)
    results=cursor.fetchall()
    #if no result, it means this raspberry pi never be used
    #do a "insert" operation immediately
    if not results:
        h1=dht22.humidity()
        t1=dht22.temperature()
        dt=time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
        #avoid to insert a illegal data
        if (h1!=-999 or h1!="-999") and (t1!=-999 or t1!="-999"):   
            sqlinsertFirst="insert into TemAndHumTest (date,temperature,humidity) values ('%s','%s','%s')" %(dt,t1,h1)
            cursor.execute(sqlinsertFirst)
            db.commit()
            print("already insert the first record")
        else:
            print("illegal Tvalue and Hvalue")
    else:
        #the most important thing is determine if the time interval is long enough
        aList=[]
        for i in results[0][0]:
            if i=='0' or i=='1' or i=='2' or i=='3' or i=='4' or i=='5' or i=='6' or i=='7' or i=='8' or i=='9':
                aList.append(i)
        aStr=''.join(aList)
        #type swift(string -> int) for subtraction
        oldOneData=int(aStr)
        print(oldOneData)
        dtNew=time.strftime('%Y%m%d%H%M%S',time.localtime(time.time()))
        print(dtNew)
        NewOneDate=int(dtNew)
        if NewOneDate-oldOneData>10000:#10000is 10minut
            humditityData=dht22.humidity()
            temperatureData=dht22.temperature()
            dateData=time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time()))
            #avoid illegal data
            print(humditityData)
            print(temperatureData)
            sqlinsert="insert into TemAndHumTest (date,temperature,humidity) values ('%s','%s','%s')" %(dateData,temperatureData,humditityData)
            if (humditityData!=-999 or humditityData!="-999") and (temperatureData!=-999 or temperatureData!="-999"):
                cursor.execute(sqlinsert)
                db.commit()
                print("already insert the new record")
            else:
                print("data is illeagal")
        else:
            print("the duration less than 10 minutes, no data will be insert into mysql")
    db.close()
#if don't have time.sleep, the action of wheel can not implement 
def movingForwardFunction():
    forward() 
    time.sleep(0.5)
    print('moving forward')

def movingBackFunction():
    back()
    time.sleep(0.5)
    print('moving back')

def movingLeftFunction():
    left()
    time.sleep(0.5)
    print('moving left')

def movingRightFunction():
    right()
    time.sleep(0.5)
    print('moving right')

def camUpFunction():
    servo_up()
    print('cam up')

def camDownFunction():
    servo_Upwatch()
    print("upwatch")
def camLeftFunction():
    servo_right()
    print('cam left')

def camRightFunction():
    servo_left()
    print('cam right')

#socket communication protocol, which can be considered as an interpreter
def dispatch(cmd):
    global func4, func5, func6, funcxy,funcmysql
    if cmd == '1':
        funcxy.run()
        func4.run()
    elif cmd == 'end1':
        funcxy.stop()
        func4.stop()
    elif cmd == '2':
        funcxy.run()
        func5.run()
    elif cmd == 'end2':
        funcxy.stop()
        func5.stop()
    elif cmd == '3':
        funcxy.run()
    elif cmd == 'end3':
        funcxy.stop()
    elif cmd == '4':
        funcxy.run()
        func6.run()
    elif cmd == 'end4':
        funcxy.stop()
        func6.stop()
    elif cmd== '5':
        #this cmd don't need see the humidity and tempreture.(funxy)
        funcmysql.run()
    elif cmd=='end5':
        funcmysql.stop()
    elif cmd == 'movingForward':
        movingForwardFunction()
    elif cmd == 'movingBack':
        movingBackFunction()
    elif cmd == 'movingLeft':
        movingLeftFunction()
    elif cmd == 'movingRight':
        movingRightFunction()
    elif cmd == 'camUp':
        camUpFunction()
    elif cmd == 'camDown':
        camDownFunction()
    elif cmd == 'camLeft':
        camLeftFunction()
    elif cmd == 'camRight':
        camRightFunction()
    else:
        carstop() 
      
        

# 创建新的后台子线程，并且可以暂停
# create new back-end subthread, which is able to be suspended
class FuncWrapper:
    def __init__(self, func, sock, *args):
        self.cond = threading.Condition()
        self.running = False
        self.sock = sock
        self._func = func
        self.tid = threading.Thread(target = self.func, args = args)
        self.tid.setDaemon(True)
        self.tid.start()

    #  begin to execute a thread
    def run(self):
        if not self.running:
            tid = threading.Thread(target = self._run)
            tid.setDaemon(True)
            tid.start()

    def _run(self):
        while True:
            self.cond.acquire()
            if not self.running:
                self.cond.notify()
                self.cond.release()
            else:
                self.cond.release()
                break
    # 暂停线程 to suspend the thread
    def stop(self):
        if self.running:
            tid = threading.Thread(target = self._stop)
            tid.setDaemon(True)
            tid.start()

    def _stop(self):
        self.cond.acquire()
        self.running = False
        self.cond.release()

    # thread execution function, this function is required to include a socket function 
    def func(self, *args):
        while True:
            self.cond.acquire()
            if not self.running:
                self.cond.wait()
                self.running = True
            self.cond.release()
            self._func(self.sock, *args)
#decode, remove the "#" in the receive data 
def serveFunc(sock):
    lastCmd = ''
    while True:
        data = sock.recv(4096).decode(_charset)
        if not data:
            # print("client Close.")
            break
        # print("recv data is : %s" % data)
        if data[-1] != '#':
            cmdList = data.split('#')
            cmdList[0] = lastCmd + cmdList[0]
            lastCmd = opt.pop()
        else:
            cmdList = data.split('#')
            cmdList[0] = lastCmd + cmdList[0]
            lastCmd = ''
        for cmd in cmdList:
            dispatch(cmd)

# to avoid forcing thread shutdown while other child thread are still send fields,delay to shutdown the thread
def waitClose(sock):
    # print(sock)
    time.sleep(10)
    sock.close()

def init(ip, port):

    #servo_init()    
    #set the MODE OF socket is TCP/IP
    listenSock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    listenSock.bind((ip, port))
    listenSock.listen(5)
    global func4, func5, func6, funcxy,funcmysql
    func4 = FuncWrapper(function4, None)
    func5 = FuncWrapper(function5, None)
    func6 = FuncWrapper(function6, None)
    funcxy = FuncWrapper(functionxy, None)
    funcmysql=FuncWrapper(function7,None)
    waringfunc = FuncWrapper(waringfunction, None)

    global sendLock
    sendLock = threading.Lock()
    
    #test up down servo motor (INIT)
    global upDownSingal
    pwm_UpDownServo.ChangeDutyCycle(3.5)
    time.sleep(0.5)
    pwm_UpDownServo.ChangeDutyCycle(0)
    upDownSingal=90
    

    try:
        while True:
            #3 time handshake,accept the connection requirement of client
            connsock, address = listenSock.accept()

            func4.sock = connsock
            func5.sock = connsock
            func6.sock = connsock
            funcxy.sock = connsock
            funcmysql.sock=connsock
            waringfunc.sock = connsock
            waringfunc.run()

            serveFunc(connsock);

            waringfunc.stop();
            func4.stop()
            func5.stop()
            func6.stop()
            funcmysql.stop()
            funcxy.stop()

            closeTid = threading.Thread(target = waitClose, args = [connsock])
            closeTid.setDaemon(True)
            closeTid.start()
    finally:
        listenSock.close()
#https://www.sheffield.ac.uk
#wh
if __name__ == '__main__':
    ip = '143.167.144.4'
    #143.167.145.134  143.167.144.158 
    #10.96.200.33 
    #143.167.147.126
    #143.167.145.101
    port = 29343
    init(ip, port)


