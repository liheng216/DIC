# DIC
Apriori’ algorithm improves.
package com.liheng.Update;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;





public class AprioriDIC {

	//全局项集长度
	private int itemNums;
	private static int numTransactions;
	private int M;
	private int numRead;
	private int dbRun = 0;
	private static ArrayList<Candidate> candidates = new ArrayList<Candidate>();
	private static double minSup = 0.5;
	private static double minConf = 0.01; // 最小置信度阀值
	private static List<CItem> listCItem = new ArrayList<CItem>();
	private static Map<List<String>, Double> listFItem = new HashMap<List<String>, Double>();// 记录频繁项集并计算支持度
	private String tranPath;
	

	public static void main(String[] args) {
		//数据集路径
		String PATH = "e:\\AprioriData\\test\\data.txt";
		//事务集个数
		int tranNum = 4;
		//全局项集个数
		int alltranNum = 5;
		//一次读取的事务数
		int m = 3;
		
		AprioriDIC dic = new AprioriDIC(PATH, tranNum, alltranNum, m, minSup, minConf);

		dic.dicProcess();
		for (int j = 0; j < candidates.size(); j++) {
			if (candidates.get(j).count / (double) numTransactions >= minSup) {
				System.out.println(candidates.get(j).getItemSet() + "    " + Double.valueOf(candidates.get(j).count));
			//	listFItem.put(candidates.get(j).getItemSet(), Double.valueOf(candidates.get(j).count));
			}
		}
		
		for (int n = 0; n < candidates.size(); n++) {
			//if itemsetNum
			Candidate candidate = candidates.get(n);
			if (candidate.itemsetNum>=2) {
				Citem(candidate.getItemSet(), listFItem.get(candidate.getItemSet()));
			}
		}

	}


	public AprioriDIC(String tranPath, int numTransactions, int itemNums, int M, double minSup,double minConf) {
		this.tranPath =tranPath;
		this.numTransactions = numTransactions;
		this.itemNums = itemNums;
		this.M = M;
		this.minSup = minSup;
		this.minConf = minConf;
	}

	public void dicProcess() {
		FileInputStream file_in;
		BufferedReader data_in;

		genFirstItem();
		do {
			dbRun++;
		//	System.out.println("dbrun=" + dbRun);
			try {
				numRead = 0;
				file_in = new FileInputStream(tranPath);
				data_in = new BufferedReader(new InputStreamReader(file_in));

				while (numRead < numTransactions) {
					loadTransactions(data_in);
					updateCandidates();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} while (dashFound());

	}
	
	
	/**
	 * 生成1-项集
	 */
	public void genFirstItem() {

		for(int i = 1; i <= itemNums; i++){
			List<String> list = new ArrayList<>(1);
			list.add(Integer.toString(i));
			candidates.add(new Candidate(0, list, 0, numTransactions, 1));
		}
	}

	/**
	 * 加载M条事务并遍历候选项集集合 判断每个非实候选集合是否在该事务内 如果存在则加一
	 * @throws IOException 
	 */
	private void loadTransactions(BufferedReader data_in) throws IOException {
		List<String> input;
		// 遍历M条事务
		for (int t = 0; t < M; t++) {
			// 此处待修改
			String inputString = data_in.readLine();
			if (inputString == null) {
				break;
			}
			input = convertFormat(inputString);
			numRead++;
		//	System.out.println("numRead="+ numRead);
			// 遍历每一个虚项集
			for (int c = 0; c < candidates.size(); c++) {
				// 判断每一个虚候选集是否在该事务内
				if (!candidates.get(c).solid
						&& input.containsAll(candidates.get(c).getItemSet())) {
					candidates.get(c).count++;
				}
			}
		}
	}

	/**
	 * 将读进的一条数据转换成List格式
	 */
	private List<String> convertFormat(String tuple){
		List<String> list = new ArrayList<>();
		StringTokenizer st = new StringTokenizer(tuple, " ");
		while(st.hasMoreTokens()){
			list.add(st.nextToken());
		}
		return list;
	}

	/**
	 * 更新候选项集信息 即四种变化之间的转换 如果该项集为方形 则生成新的候选项集
	 */
	private void updateCandidates() {
		// 遍历所有项集
		for (int c = 0; c < candidates.size(); c++) {

			// 对于虚项集 如果已经遍历过一遍数据库 则变成实项集
			if (!candidates.get(c).solid
					&& candidates.get(c).startNum == numRead
					&& candidates.get(c).dbRun == dbRun - 1) {
				candidates.get(c).solid = true;
			}

			//最小支持度大于阀值 则变成方形
			if (candidates.get(c).circle
					&& candidates.get(c).count / (double) numTransactions >= minSup) {
				candidates.get(c).circle = false;
				makeNewCandidates(candidates.get(c));
			}
		}
	}

	/**
	 * 创建新的项集   传入已知频繁项集
	 * @param cand
	 */
	private void makeNewCandidates(Candidate cand){
		List<String> newCandidateList;
		Candidate tempCand, secondCand;
		for(int c = 0; c < candidates.size(); c++){
			newCandidateList = new ArrayList<String>();
			secondCand = candidates.get(c);

			if(secondCand.itemsetNum == cand.itemsetNum && !secondCand.circle){
				Set<String> newSet = new TreeSet<>();
				List<String> itemOne = cand.itemset;
				List<String> itemTwo = secondCand.itemset;
				//合并
				for(int i = 0; i < secondCand.itemsetNum; i++){
					newSet.add(itemOne.get(i));
					newSet.add(itemTwo.get(i));
				}
				
				List<String> newItem = new ArrayList<>(newSet);
				if(!isDuplicateCandidate(newItem) && newItem.size() == itemOne.size()+1){
					candidates.add(new Candidate(0, newItem, dbRun, numRead, newItem.size()));
				}
			}
			//这里导致内存溢出
			listFItem.put(candidates.get(c).getItemSet(), Double.valueOf(candidates.get(c).count));
		//	System.out.println(candidates.get(c).itemsetNum + "   " + Double.valueOf(candidates.get(c).count));
		}
	}

	private boolean isDuplicateCandidate(List<String> cand){
		for(int c=0; c<candidates.size(); c++)
			if(candidates.get(c).itemset.equals(cand))
				return true;
		return false;
	}

	private boolean dashFound() {
		for (int c = 0; c < candidates.size(); c++)
			if (!candidates.get(c).solid)
				return true;
		return false;
	}

	


	/**
	 * 基于单个频繁项集set，生成关联规则集
	 * 
	 * @param set
	 * @return
	 */
	private static void Citem(List<String> list, double totalSup) {

		List<String> a = new ArrayList<String>();
		List<String> b = new ArrayList<String>();

		String[] FItem = list.toArray(new String[0]);
		for (int i = 1; i < (1 << FItem.length) - 1; i++) {
			
			
			for (int j = 0; j < FItem.length; j++) {
				if (((1 << j) & i) != 0) {
					a.add(FItem[j]);
				}
			}
			for (int j = 0; j < FItem.length; j++) {
				if (((1 << j) & (~i)) != 0) {
					b.add(FItem[j]);
				}
			}
			double conf =  totalSup/listFItem.get(a);
			if (conf > minConf) {
				CItem e = new CItem();
				e.setItemSetA(a);
				e.setItemSetB(b);
				e.setConfidence(conf);
				listCItem.add(e);
				
				//输出关联规则
				System.out.println(Arrays.toString(e.getItemSetA().toArray()) + " --> " + e.getItemSetB().toString() + " : "
						+ e.getConfidence());
			}
			a.clear();
			b.clear();
		}
	}
}
