from message.api import MessageService
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer

import smtplib
from email.mime.text import MIMEText
from email.header import Header

sender = "imoocd@163.com"
authCode = "aA111111"

# coding: utf-8
class MessageServiceHandler:

    def sendMobileMessage(self, mobile, message):
        print("send mobile message: " + mobile + "  " + message)
        return True

    def sendEmailMessage(self, email, message):
        print("send email message " + email + " " + message + "\n")
        obj = MIMEText(message, "plain", "utf-8")
        obj["From"] = sender
        obj["To"] = email

        obj["Subject"] = Header("慕课网邮件", "utf-8")

        try:
            smtpObj = smtplib.SMTP("smtp.163.com")
            smtpObj.login(sender, authCode)
            smtpObj.sendmail(sender, [email], obj.as_string())
        except smtplib.SMTPException as e:
            print("send email failed with " + e)
            return False

        return True


if __name__ == '__main__':
    handler = MessageServiceHandler()
    processor = MessageService.Processor(handler)
    print("main start")
    transport = TSocket.TServerSocket("127.0.0.1", "9090")
    tfactory = TTransport.TFramedTransportFactory()
    pfactory = TBinaryProtocol.TBinaryProtocolAcceleratedFactory()

    server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)
    print("Python thrift server started ... ")
    server.serve()
    print("Python thrift server exit ... ")
