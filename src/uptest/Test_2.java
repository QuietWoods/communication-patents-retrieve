package uptest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Test_2 {

	public void method1(String file) {
		FileWriter fw = null;
		try {
			// 如果文件存在，则追加内容；如果文件不存在，则创建文件
			File f = new File(file);
			fw = new FileWriter(f, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(fw);
		// pw.println("");
		pw.flush();
		try {
			fw.flush();
			pw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void method2(String file, String conent) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			out.write(conent + "\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void method3(String fileName, String content) {
		try {
			// 打开一个随机访问文件流，按读写方式
			RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			randomFile.seek(fileLength);
			randomFile.writeBytes(content + "\r\n");
			randomFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws UnsupportedEncodingException {
		Element element = null;
		// 可以使用绝对路径

		String string = new String("201480001710NEW");
		String str = "E:\\通讯专利文本\\" + string + ".xml";
		File file = new File(URLDecoder.decode(str, "UTF-8"));
		Test_2 a = new Test_2();
		// documentBuilder为抽象不能直接实例化（将XML文件转换为DOM文件）
		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = null;
		// IgnoreDtd ignore = new IgnoreDtd();
		// DocumentBuilderFactory.setValidating(false);
		// dbf.setValidating(false);
		try {
			// 返回documentBuilderFactory对象
			dbf = DocumentBuilderFactory.newInstance();
			// dbf.setValidating(false);
			// 返回db对象用documentBuilderFactory对象获得返回documentBuilder对象
			db = dbf.newDocumentBuilder();
			// 得到一个DOM并返回给document对象
			Document dt = db.parse(file);
			// 得到一个element根元素
			element = dt.getDocumentElement();
			// 获得根节点
			System.out.println("根元素" + ".." + element.getNodeType());
			// 获得根元素下的子节点
			NodeList childNodes = element.getChildNodes();
			// 遍历这些子节点
			for (int i = 0; i < childNodes.getLength(); i++) {
				// 获得每个对应位置i的结点
				Node node1 = childNodes.item(i);
				if ("cn-bibliographic-data".equals(node1.getNodeName())) {
					// 如果结点的名称为“cn-patent-document”，则继续遍历子节点
					// 获得<cn-patent-document>下的节点
					NodeList nodeDetail = node1.getChildNodes();
					// 遍历<cn-patent-document>下的节点
					for (int j = 0; j < nodeDetail.getLength(); j++) {
						// 获得<cn-bibliographic-data>元素每一个节点
						Node detail1 = nodeDetail.item(j);
						if ("cn-publication-reference".equals(detail1.getNodeName()))
						// 子节点为cn-publication-reference，继续遍历
						// 获得<cn-publication-reference>下的结点
						{
							NodeList nodeDetail1_2 = detail1.getChildNodes();
							// 遍历<cn-publication-reference>下的结点
							for (int k = 0; k < nodeDetail1_2.getLength(); k++) {
								// 获得<cn-publication-reference>元素每一个结点
								Node detail1_2_3 = nodeDetail1_2.item(k);
								if ("document-id".equals(detail1_2_3.getNodeName())) {
									// 子节点为document-id，继续遍历
									// 获得document-id下的结点
									NodeList nodeDetail1_2_3 = detail1_2_3.getChildNodes();
									// 遍历document-id下的结点
									for (int m = 0; m < nodeDetail1_2_3.getLength(); m++) {
										// 获得document-id元素每一结点
										Node detail1_2_3_4 = nodeDetail1_2_3.item(m);
										if ("country".equals(detail1_2_3_4.getNodeName())) {
											// 子节点为country，输出
											System.out.println("country:  " + detail1_2_3_4.getTextContent());
										} else if ("doc-number".equals(detail1_2_3_4.getNodeName())) {
											System.out.println("doc-number: " + detail1_2_3_4.getTextContent());
										} else if ("kind".equals(detail1_2_3_4.getNodeName())) {
											System.out.println("kind: " + detail1_2_3_4.getTextContent());
										} else if ("date".equals(detail1_2_3_4.getNodeName())) {
											System.out.println("date: " + detail1_2_3_4.getTextContent());
										}
									}

								} else if ("gazette-reference".equals(detail1_2_3.getNodeName()))
								// 子节点为gazette-reference，继续遍历
								// 获得gazette-reference下的结点
								{
									NodeList nodeDetail1_2_3 = detail1_2_3.getChildNodes();
									// 遍历document-id下的结点
									for (int m = 0; m < nodeDetail1_2_3.getLength(); m++) {
										// 获得document-id元素每一结点
										Node detail1_2_3_4 = nodeDetail1_2_3.item(m);
										if ("gazette-num".equals(detail1_2_3_4.getNodeName())) {
											// 子节点为country，输出
											System.out.println("gazette-num:  " + detail1_2_3_4.getTextContent());
										} else if ("date".equals(detail1_2_3_4.getNodeName())) {
											System.out.println("date: " + detail1_2_3_4.getTextContent());
										}
									}
								}

							}
						} else if ("application-reference".equals(detail1.getNodeName())) {
							System.out.println("application-reference: "
									+ detail1.getAttributes().getNamedItem("appl-type").getNodeValue() + ". ");
							NodeList nodeDetail1_2 = detail1.getChildNodes();
							// 遍历<application-reference>下的结点
							for (int k = 0; k < nodeDetail1_2.getLength(); k++) {
								// 获得<application-reference>元素每一个结点
								Node detail1_2_3 = nodeDetail1_2.item(k);
								if ("document-id".equals(detail1_2_3.getNodeName())) {
									NodeList nodeDetail1_2_3 = detail1_2_3.getChildNodes();
									// 遍历document-id下的结点
									System.out.println("document-id");
									for (int m = 0; m < nodeDetail1_2_3.getLength(); m++) {
										// 获得document-id元素每一结点
										Node detail1_2_3_4 = nodeDetail1_2_3.item(m);
										if ("country".equals(detail1_2_3_4.getNodeName())) {
											// 子节点为country，输出
											System.out.println("country:  " + detail1_2_3_4.getTextContent());
										} else if ("doc-number".equals(detail1_2_3_4.getNodeName())) {
											System.out.println("doc-number: " + detail1_2_3_4.getTextContent());
										} else if ("kind".equals(detail1_2_3_4.getNodeName())) {
											System.out.println("kind: " + detail1_2_3_4.getTextContent());
										} else if ("date".equals(detail1_2_3_4.getNodeName())) {
											System.out.println("date: " + detail1_2_3_4.getTextContent());
										}
									}
								}

							}

						} else if ("priority-claims".equals(detail1.getNodeName())) {

							NodeList nodeDetail1_2 = detail1.getChildNodes();
							// 遍历<priority-claims>下的结点
							for (int k = 0; k < nodeDetail1_2.getLength(); k++) {
								// 获得<priority-claims>元素每一个结点
								Node detail1_2_3 = nodeDetail1_2.item(k);
								if ("priority-claim".equals(detail1_2_3.getNodeName())) {
									System.out.println("priority-claim");
									NodeList nodeDetail1_2_3 = detail1_2_3.getChildNodes();
									// 遍历priority-claim下的结点
									for (int m = 0; m < nodeDetail1_2_3.getLength(); m++) {
										// 获得priority-claim元素每一结点
										Node detail1_2_3_4 = nodeDetail1_2_3.item(m);
										if ("country".equals(detail1_2_3_4.getNodeName())) {
											// 子节点为country，输出
											System.out.println("country:  " + detail1_2_3_4.getTextContent());
										} else if ("doc-number".equals(detail1_2_3_4.getNodeName())) {
											System.out.println("doc-number: " + detail1_2_3_4.getTextContent());
										} else if ("date".equals(detail1_2_3_4.getNodeName())) {
											System.out.println("date: " + detail1_2_3_4.getTextContent());
										}
									}
								}

							}

						} else if ("classifications-ipcr".equals(detail1.getNodeName())) {
							System.out.println("classifications-ipcr");
							NodeList nodeDetail1_2 = detail1.getChildNodes();
							// 遍历<classifications-ipcr>下的结点
							for (int k = 0; k < nodeDetail1_2.getLength(); k++) {
								// 获得<classifications-ipcr>元素每一个结点
								Node detail1_2_3 = nodeDetail1_2.item(k);
								if ("classification-ipcr".equals(detail1_2_3.getNodeName())) {
									NodeList nodeDetail1_2_3 = detail1_2_3.getChildNodes();
									// 遍历priority-claim下的结点
									for (int m = 0; m < nodeDetail1_2_3.getLength(); m++) {
										// 获得priority-claim元素每一结点
										Node detail1_2_3_4 = nodeDetail1_2_3.item(m);
										if ("text".equals(detail1_2_3_4.getNodeName())) {
											// 子节点为country，输出
											System.out.println("country:  " + detail1_2_3_4.getTextContent());
										} else if ("doc-number".equals(detail1_2_3_4.getNodeName())) {
											System.out.println("doc-number: " + detail1_2_3_4.getTextContent());
										} else if ("date".equals(detail1_2_3_4.getNodeName())) {
											System.out.println("date: " + detail1_2_3_4.getTextContent());
										}
									}
								}

							}

						} else if ("invention-title".equals(detail1.getNodeName())) {
							System.out.println("invention-title: " + detail1.getTextContent());
							a.method1("E:\\通讯专利文本\\通讯专利文本_txt\\" + string + ".txt");
							a.method2("E:\\通讯专利文本\\通讯专利文本_txt\\" + string + ".txt", detail1.getTextContent());
							NodeList nodeDetail1_2 = detail1.getChildNodes();

						} else if ("abstract".equals(detail1.getNodeName())) {
							System.out.println("abstract: " + detail1.getTextContent());
							String temp = detail1.getTextContent().replaceAll("\r", "");
							temp = temp.replaceAll("\\s*", "");
							a.method2("E:\\通讯专利文本\\通讯专利文本_txt\\" + string + ".txt", "摘要\r\n" + temp);
							NodeList nodeDetail1_2 = detail1.getChildNodes();

						}
					}
				} else if ("application-body".equals(node1.getNodeName())) {
					System.out.println("application-body");
					NodeList nodeDetail1_2 = node1.getChildNodes();
					// 遍历<classifications-ipcr>下的结点
					for (int k = 0; k < nodeDetail1_2.getLength(); k++) {
						// 获得<classifications-ipcr>元素每一个结点
						Node detail1_2_3 = nodeDetail1_2.item(k);
						if ("description".equals(detail1_2_3.getNodeName())) {
							System.out.println("description :");
							NodeList nodeDetail1_2_3 = detail1_2_3.getChildNodes();

							// 遍历priority-claim下的结点
							for (int m = 0; m < nodeDetail1_2_3.getLength(); m++) {
								// 获得priority-claim元素每一结点

								Node detail1_2_3_4 = nodeDetail1_2_3.item(m);
								if ("invention-title".equals(detail1_2_3_4.getNodeName())) {
									// 子节点为cn-applicant，输出
									System.out.println("invention-title:  ");
								} else if ("p".equals(detail1_2_3_4.getNodeName())) {
									System.out.println("" + detail1_2_3_4.getTextContent());
									String temp = detail1_2_3_4.getTextContent().replaceAll("\r", "");
									temp = temp.replaceAll("\\s*", "");
									a.method2("E:\\通讯专利文本\\通讯专利文本_txt\\" + string + ".txt", temp);
								}
							}
						} else if ("claims".equals(detail1_2_3.getNodeName())) {
							System.out.println("claims :");
							NodeList nodeDetail1_2_3 = detail1_2_3.getChildNodes();
							String temp = detail1_2_3.getTextContent().replaceAll("\r", "");
							temp = temp.replaceAll("\\s*", "");
							a.method2("E:\\通讯专利文本\\通讯专利文本_txt\\" + string + ".txt", temp);

							// 遍历priority-claim下的结点
							for (int m = 0; m < nodeDetail1_2_3.getLength(); m++) {
								// 获得priority-claim元素每一结点

								Node detail1_2_3_4 = nodeDetail1_2_3.item(m);
								if ("claim".equals(detail1_2_3_4.getNodeName())) {
									// 子节点为cn-applicant，输出
									System.out.println("" + detail1_2_3_4.getTextContent());
								}
							}
						}

					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
