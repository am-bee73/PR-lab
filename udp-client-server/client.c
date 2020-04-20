#include<sys/socket.h>
#include<netdb.h>
#include<string.h>
#include<stdlib.h>
#include<stdio.h>
#define BUFSIZE 80
#define PORT 4000
#define SA struct sockaddr

int main()
{
	char buff[BUFSIZE];
	int sockfd,len,n;
	struct sockaddr_in servaddr;
	//crearea socketului udp
	sockfd=socket(AF_INET,SOCK_DGRAM,0);
	
	if(sockfd==-1)
	{
		printf("socket creation failed...\n");
		exit(0);
	}
	else
	printf("Socket successfully created..\n");
	bzero(&servaddr,sizeof(len));
	servaddr.sin_family=AF_INET; // addres family domeniu de comunicare actual - AF_INET pentru protocolul ipv4
	servaddr.sin_addr.s_addr=inet_addr("127.0.0.1"); // internet address
	servaddr.sin_port=htons(PORT); // port nummber
	len=sizeof(servaddr);

	for(;;)
	{
		printf("\nEnter string : ");
		n=0;
		while((buff[n++]=getchar())!='\n');
		// trimite cererea serverului
		sendto(sockfd,buff,sizeof(buff),0,(SA *)&servaddr,len);
		bzero(buff,sizeof(buff));
		// primeste raspundul de la server
		recvfrom(sockfd,buff,sizeof(buff),0,(SA *)&servaddr,&len);
		printf("From Server : %s\n",buff);
		
		if(strncmp("exit",buff,4)==0)
		{
			printf("Client Exit...\n");
			break;
		}
	}
close(sockfd);
}
