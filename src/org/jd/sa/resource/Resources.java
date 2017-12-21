package org.jd.sa.resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import org.jd.sa.process.FileStructure;

public class Resources {

	private static boolean _stopSignal = false;
	public static final int FRAME_WIDTH = 1272;
	public static final int FRAME_HEIGHT = 705;

	public static final String getFormattedCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(new Date());
	}

	public static final String getDurationFormatted(long duration) {
		return String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(duration),
				TimeUnit.MILLISECONDS.toMinutes(duration)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(duration)),
				TimeUnit.MILLISECONDS.toSeconds(duration)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(duration)));

	}

	public static long getTotalItem(Path path) {

		final AtomicLong count = new AtomicLong(0);

		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) {

					if (_stopSignal) {
						return FileVisitResult.TERMINATE;
					}
					if (!attrs.isDirectory())
						count.addAndGet(1);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file,
						IOException exc) {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			throw new AssertionError(
					"walkFileTree will not throw IOException if the FileVisitor does not");
		}
		if (_stopSignal) {
			_stopSignal = false;
			return -1L;
		} else {
			return count.get();
		}
	}

	public static final void terminateScan() {
		_stopSignal = true;
	}

	public static void sort(List<FileStructure> children) {
		Collections.sort(children, new Comparator<FileStructure>() {

			@Override
			public int compare(FileStructure fs1, FileStructure fs2) {
				File f1 = fs1.getPath();
				File f2 = fs2.getPath();

				if (f1.isFile() && f2.isDirectory())
					return 1;

				if (f2.isFile() && f1.isDirectory())
					return -1;

				if (f1.isDirectory() && f2.isDirectory())
					return f1.compareTo(f2);

				String fn1 = f1.getAbsolutePath();
				String fn2 = f2.getAbsolutePath();
				int extPoint1 = fn1.lastIndexOf('.');
				int extPoint2 = fn2.lastIndexOf('.');

				if (extPoint1 == -1 && extPoint2 == -1)
					return fn1.compareToIgnoreCase(fn2);

				if (extPoint1 == -1)
					return 1;

				if (extPoint2 == -1)
					return -1;

				String ext1 = fn1.substring(extPoint1);
				String ext2 = fn2.substring(extPoint2);

				return ext1.compareToIgnoreCase(ext2);
			}
		});
	}

	public static String[] getNormalizedSize(long bytes) {
		double dBytes = (double) bytes;

		if (bytes < 1024)
			return new String[] { String.valueOf(bytes), "bytes" };

		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(2);

		if (bytes < 1024 * 1024 - 1)
			return new String[] { format.format(dBytes / 1024), "kb" };

		if (bytes < 1024 * 1024 * 1024 - 1)
			return new String[] { format.format(dBytes / (1024 * 1024)), "mb" };

		return new String[] { format.format(dBytes / (1024 * 1024 * 1024)),
				"gb" };
	}

	public static long getGeneralizedSize(double dSize, String unit) {
		switch (unit) {
		case "bytes":
			return (long) dSize;
		case "kb":
			return (long) (dSize * 1024);
		case "mb":
			return (long) (dSize * 1024 * 1024);
		case "gb":
			return (long) (dSize * 1024 * 1024 * 1024);
		default:
			break;
		}
		return 0;
	}

	public static final void setLookAndFeel() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
}
