package org.jootnet.m2client.util;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 文件二进制读取类
 * <br>
 * 此类继承{@link RandomAccessFile}，添加一系列以<b>LE</b>结尾的函数将读取的字节以<b>Little-Endian</b>格式返回
 * 
 * @author johness
 */
public final class BinaryReader extends RandomAccessFile {

	public BinaryReader(File file) throws FileNotFoundException {
		super(file, "r");
		try {
			preRead();
		} catch (IOException e) {
		}
	}
	
	/**
	 * 从流中读取一个短整形数据
	 * <br>
	 * 流位置向前推进两个字节
	 * 
	 * @return 一个短整形，以Little-Endian格式返回
	 * @throws IOException 文件已达到末尾
	 */
	public final short readShortLE() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short)((ch2 << 8) + (ch1 << 0));
    }
	
	/**
	 * 从流中读取一个无符号短整形数据
	 * <br>
	 * 流位置向前推进两个字节
	 * 
	 * @return 一个无符号短整形，以Little-Endian格式返回
	 * @throws IOException 文件已达到末尾
	 */
	public final int readUnsignedShortLE() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch2 << 8) + (ch1 << 0);
    }
	
	/**
	 * 从流中读取一个双字节字符
	 * <br>
	 * 流位置向前推进两个字节
	 * 
	 * @return 一个双字节字符，以Little-Endian格式返回
	 * @throws IOException 文件已达到末尾
	 */
	public final char readCharLE() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (char)((ch2 << 8) + (ch1 << 0));
    }
	
	/**
	 * 从流中读取一个四字节整形
	 * <br>
	 * 流位置向前推进四个字节
	 * 
	 * @return 一个整形，以Little-Endian格式返回
	 * @throws IOException 文件已达到末尾
	 */
	public final int readIntLE() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        int ch3 = this.read();
        int ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
    }
	
	/**
	 * 从流中读取一个八字节长整形
	 * <br>
	 * 流位置向前推进八个字节
	 * 
	 * @return 一个长整形，以Little-Endian格式返回
	 * @throws IOException 文件已达到末尾
	 */
	public final long readLongLE() throws IOException {
        return ((long)(readIntLE()) & 0xFFFFFFFFL) + (readIntLE() << 32);
    }
	
	/**
	 * 从流中读取一个单精度浮点数
	 * <br>
	 * 流位置向前推进四个字节
	 * 
	 * @return 一个单精度浮点数，以Little-Endian格式返回
	 * @throws IOException 文件已达到末尾
	 */
	public final float readFloatLE() throws IOException {
        return Float.intBitsToFloat(readIntLE());
    }
	
	/**
	 * 从流中读取一个双精度浮点数
	 * <br>
	 * 流位置向前推进八个字节
	 * 
	 * @return 一个双精度浮点数，以Little-Endian格式返回
	 * @throws IOException 文件已达到末尾
	 */
	public final double readDoubleLE() throws IOException {
        return Double.longBitsToDouble(readLongLE());
    }
	
	
	private byte[] preReadBuffer = new byte[1024 * 4]; // 预读4k
	private int posInBuffer = 0; // 当前读取的指针数据在预读缓冲区的索引
	private int bufferContentLen = 0; // 缓冲区有效数据长度
	@Override
	public int read() throws IOException {
		if(posInBuffer >= bufferContentLen) { // 预读的读完了，再次预读
			preRead();
		}
		return preReadBuffer[posInBuffer++] & 0xff;
	}
	
	@Override
	public int skipBytes(int n) throws IOException {
		long pos;
        long len;
        long newpos;

        if (n <= 0) {
            return 0;
        }
        pos = getFilePointer() + posInBuffer;
        len = length();
        newpos = pos + n;
        if (newpos > len) {
            newpos = len;
        }

		posInBuffer += (int) (newpos - pos);
		return (int) (newpos - pos);
	}
	
	@Override
	public void seek(long pos) throws IOException {
		super.seek(pos);
		posInBuffer = preReadBuffer.length; // seek后需重新预读
	}
	
	private void preRead() throws IOException {
		super.seek(getFilePointer() + posInBuffer);
		bufferContentLen = read(preReadBuffer);
		super.seek(getFilePointer() - bufferContentLen);
		if(bufferContentLen == -1)
			throw new IOException("end of file");
		posInBuffer = 0;
	}
}
