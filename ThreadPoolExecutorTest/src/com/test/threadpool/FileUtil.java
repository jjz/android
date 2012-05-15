package com.test.threadpool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;

public class FileUtil {
	private int MB = 1024;
	private int mTimeDiff = 10000;
	private int CACHE_SIZE = 10;
	private int FREE_SD_SPACE_NEEDED_TO_CACHE = 500;

	public static ArrayList<String> getMusicListOnSys(File file) {

		ArrayList<String> fileList = new ArrayList<String>();
		getFileList(file, fileList);
		return fileList;
	}

	/**
	 * @param path
	 * @param fileList
	 *            注意的是并不是所有的文件夹都可以进行读取的，权限问题
	 */
	private static void getFileList(File path, ArrayList<String> fileList) {
		// 如果是文件夹的话
		if (path.isDirectory()) {
			// 返回文件夹中有的数据
			File[] files = path.listFiles();
			// 先判断下有没有权限，如果没有权限的话，就不执行了
			if (null == files)
				return;

			for (int i = 0; i < files.length; i++) {
				getFileList(files[i], fileList);
			}
		}
		// 如果是文件的话直接加入
		else {

			// 进行文件的处理
			String filePath = path.getAbsolutePath();
			// 文件名
			String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
			// 添加
			fileList.add(filePath);
		}
	}

	/**
	 * sdCard exist
	 * 
	 * @return
	 */
	public static boolean existSdCard() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 清空SD卡上的图片数据
	 * 
	 * @param dirPath
	 */
	private void removeCache(String dirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null)
			return;
		int dirSize = 0;
		for (int i = 0; i < files.length; i++) {
			dirSize += files[i].length();

		}
		if (dirSize > CACHE_SIZE * MB
				|| FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			int removeFactor = (int) ((0.4 * files.length) + 1);
			Arrays.sort(files, new FileLastMadifSort());
			// Log.i(this.toString(), "Clear some  expiredcache files");
			for (int i = 0; i < removeFactor; i++) {
				files[i].delete();
			}
		}
	}

	/**
	 * 修改文件的最后修改时间
	 * 
	 * @param dir
	 * @param fileName
	 */
	public static void updateFileTime(String dir, String fileName) {
		File file = new File(dir, fileName);
		long nowModifiedTime = System.currentTimeMillis();
		file.setLastModified(nowModifiedTime);
	}

	/**
	 * 删除过期文件
	 * 
	 * @param dirPath
	 * @param fileName
	 */
	private void removeExpiredCache(String dirPath, String fileName) {
		File file = new File(dirPath, fileName);
		if (System.currentTimeMillis() - file.lastModified() > mTimeDiff)
			file.delete();
	}

	/**
	 * 根据文件的最后修改时间进行排序
	 * 
	 * @author jjz
	 * 
	 */
	class FileLastMadifSort implements Comparator<File> {

		public int compare(File arg0, File arg1) {
			// TODO Auto-generated method stub
			if (arg0.lastModified() > arg1.lastModified())
				return 1;
			else if (arg0.lastModified() == arg1.lastModified())
				return 0;
			else
				return -1;
		}

	}

	/**
	 * sd卡剩余的空间
	 * 
	 * @return
	 */
	public int freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
				.getBlockSize()) / MB;
		return (int) sdFreeMB;
	}

	/**
	 * 用当前时间取得的图片命名(新建文件)
	 * 
	 * @return
	 */
	public static String getPhotoFileName() {
		return Long.toString(System.currentTimeMillis());
	}

	/**
	 * 返回文件目录，如果目录不存在创建目录
	 * 
	 * @param dirName
	 * @return
	 */
	public static File getImageDir(String dirName) {
		File dir = new File(Environment.getExternalStorageDirectory(), dirName);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * url转换为file
	 * 
	 * @param activity
	 * @param uri
	 * @return
	 */
	public static File convertUriToFile(Activity activity, Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = activity.managedQuery(uri, proj, null, null,
				null);
		int actual_image_column_index = actualimagecursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor
				.getString(actual_image_column_index);
		File src = new File(img_path);
		return src;
	}

	public static void saveFile(String str, File tar) throws Exception {
		FileOutputStream outputStream = new FileOutputStream(tar);
		outputStream.write(str.getBytes());
		outputStream.flush();
		outputStream.close();

	}

	public static String readFile(File file) throws Exception {
		FileInputStream is = new FileInputStream(file);
		byte[] bytes = new byte[1024];
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		while (is.read(bytes) != -1)
			arrayOutputStream.write(bytes, 0, bytes.length);
		is.close();
		arrayOutputStream.close();
		return new String(arrayOutputStream.toByteArray());

	}

	/**
	 * 复制文件
	 * 
	 * @param src
	 * @param tar
	 * @throws Exception
	 */
	public static void copyFile(File src, File tar) throws Exception {

		if (src.isFile()) {
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			try {
				InputStream is = new FileInputStream(src);
				bis = new BufferedInputStream(is);
				OutputStream op = new FileOutputStream(tar);
				bos = new BufferedOutputStream(op);
				byte[] bt = new byte[8192];
				int len = bis.read(bt);
				while (len != -1) {
					bos.write(bt, 0, len);
					len = bis.read(bt);
				}
				bis.close();
				bos.close();
			} catch (Exception e) {
				e.printStackTrace();

			} finally {

			}

		} else if (src.isDirectory()) {
			File[] files = src.listFiles();
			tar.mkdir();
			for (int i = 0; i < files.length; i++) {
				copyFile(files[i].getAbsoluteFile(),
						new File(tar.getAbsoluteFile() + File.separator
								+ files[i].getName()));
			}
		} else {
			throw new FileNotFoundException();
		}

	}

}
