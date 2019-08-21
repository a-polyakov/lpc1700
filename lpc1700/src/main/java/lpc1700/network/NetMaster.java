package lpc1700.network;

import lpc1700.Index;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: Поляков Александа Александрович
 * Date: 11.09.2007
 * Time: 7:56:15
 * Сетевое соединение с мастером
 * <p/>
 * OP_CYL_POS-DR_CYL_POS!=DIF_CYL_POS
 * медленная скорость работы при ожидании задает мастер или vax
 * проверять время посылки если оно не корректно игнорировать данные
 */
public class NetMaster extends Thread
{
	private boolean stop;
	private Index index;
	private ServerSocket serverSocket;

	public NetMaster(Index index)
	{
		this.index = index;
	}

	// запустить сервер
	public void run()
	{
		try
		{
			serverSocket = new ServerSocket(Index.SOCKET_PORT_MASTER);
			index.netEventRun(this);
			Socket socket;
			InputStream in;
			while (!stop)
				try
				{
					// ждать подключения
					socket = serverSocket.accept();
					in = socket.getInputStream();
					index.netEventConnect(this);
					while (!stop)
					{
						// получить данные
						readStan(in);
						// отобразить изменение данных
						index.newData();
					}
					in.close();
					socket.close();
					index.netEventStop(this);
				}
				catch (EOFException e)
				{
					index.netEventClose(this);
				}
				catch (IOException e)
				{
					index.netEventError(this, e.getMessage());
				}
		}
		catch (IOException e)
		{
			index.netEventError(this, "Ошибка при инициализации сервера: " + e.getMessage());
			index.netEventStop(this);
		}
	}

	// остановить сервер
	public void close()
	{
		stop = true;
		try
		{
			if (serverSocket != null)
				serverSocket.close();
		}
		catch (IOException e)
		{
		}
	}

	private void readStan(InputStream in) throws IOException
	{
		readDate(in);
		readLogVal(in);
		readAnaMas(in);
		readAnaAgc(in);
		readAnaHgc(in);
		readAnaPlc1(in);
		readAnaPlc2(in);
		readAnaGaug(in);
		readAnaMariou(in);
		readAlarm(in);
	}

	// Дата посылки 8 байт
	private void readDate(InputStream in) throws IOException
	{
		index.stan.timeMaster.setDay((byte) in.read());
		index.stan.timeMaster.setMonth((byte) in.read());
		index.stan.timeMaster.setYear((short) (2000 + in.read()));
		index.stan.timeMaster.setHour((byte) in.read());
		index.stan.timeMaster.setMin((byte) in.read());
		index.stan.timeMaster.setSec((byte) in.read());
		index.stan.timeMaster.setMsec((short) (in.read() * 10));
		int empty = in.read();
	}

	// 32+20+12+24+24=112
	private void readLogVal(InputStream in) throws IOException
	{
		int i;
// DIGITAL SIGNALS FROM MASTER
//	Word 1
		i = readWord(in);
		//0 L_SIS_R4A		! всегда 0 - сигнал наличия раската в клети
		//1 L_SIS_O2  =  (L_SIS_R4A     + 1);	// пусто
		//index.stan.s_5=(i&1<<2)!=0;	//2 L_SIS_F5	сигнал наличия раската в клети
		//index.stan.s_6=(i&1<<3)!=0;	//3 L_SIS_F6
		//index.stan.s_7=(i&1<<4)!=0;	//4 L_SIS_F7
		//index.stan.s_8=(i&1<<5)!=0;	//5 L_SIS_F8
		//index.stan.s_9=(i&1<<6)!=0;	//6 L_SIS_F9
		//index.stan.s_10=(i&1<<7)!=0;	//7 L_SIS_F10
		//8 L_SIS_BOB сигнал наличия раската на маталках
		//9			!
		//10 SIMU_ON		!
		//11 SIS_T2		!
		//12 SIS_T3		!
		//13		!
		//14		!
		//15 L_BP_PERTU_BOB		! нажата кнопка некоректной работы на пульте моталок

//	Word 2
		i = readWord(in);
		//0 L_DETEC_HMD1		пусто
		//1 L_DETEC_HMD2		!!! SIS_O2
		//2 L_GW1_MES			пусто
		//3 L_GW2_MES			пусто
		//4 L_PYRO1_MES			сигнал наличия раската под пирометром
		//5 L_PYRO2_MES
		//6 L_PYRO3_MES
		//7 L_PYRO4_MES
		//8 L_PYRO5_MES
		//9 L_BLOC_NEW_PRESET	блокировать навые пресеты
		//10 L_SHEARS_ROLL_FF	пусто (! было ножницы режут)
		//11 VOY_LAM
		//12 VOY_MAIN
		//13 VOY_AUTO
		//14 VOY_SAUTO
		//15 VOY_MANU

//	Word 3
		i = readWord(in);
		index.stan.t1.work = (i & 1) == 0;		//0 L_DEF_PYRO1	пирометр не работает
		index.stan.t2.work = (i & 1 << 1) == 0;	//1 L_DEF_PYRO2
		index.stan.t3.work = (i & 1 << 2) == 0;	//2 L_DEF_PYRO3
		index.stan.t4.work = (i & 1 << 3) == 0;	//3 L_DEF_PYRO4
		index.stan.t5.work = (i & 1 << 4) == 0;	//4 L_DEF_PYRO5
		//5 L_DEF_HMD1			пусто
		//6 L_DEF_HMD2
		//7 L_DEF_GW1
		//8 L_DEF_GW2
		//9 L_ARRET_ANORMAL		! нажата кнопка аварийной остановки
		//10 L_BP_PERTU_OD1		! нажата кнопка некоректной работы
		//11 L_BOOT_PULSE		!
		//12 L_TEST_AUTO		!

//	Word 4
		i = readWord(in);
		//0 L_MODE_ROLL			! режим прокатки
		//1 L_MODE_MAINT		! режим обслуживания
		//2 L_MODE_AUTO			! автоматический режим прокатки
		//3 L_MODE_SEMI			! полуавтоматический режим прокатки
		//4 L_MODE_MANU			! ручной режим прокатки
		//5 L_BP_PDI			кнопка для подтверждения свойств сляба
		//6 L_BP_REGL			! разгон включен
		//7 L_ARRET				! кнопка аварийной остановки
		//8 L_VIT_LEN			медленая скорость работы двигателей при ожидании
		//9 L_VIT_NORM			нормальная скорость работы двигателей при ожидании
		//10 L_ACQ_DEF_OD1		!
		//11 L_ESS_LAMP_OD1		!

//	Word 5
		i = readWord(in);
		index.stan.klet4a.open = (i & 1) != 0;		//0 L_R4A_OPEN клеть открыта
		//1 L_O2_OPEN
		index.stan.F5.open = (i & 1 << 2) != 0;		//2 L_F5_OPEN
		index.stan.F6.open = (i & 1 << 3) != 0;		//3 L_F6_OPEN
		index.stan.F7.open = (i & 1 << 4) != 0;		//4 L_F7_OPEN
		index.stan.F8.open = (i & 1 << 5) != 0;		//5 L_F8_OPEN
		index.stan.F9.open = (i & 1 << 6) != 0;		//6 L_F9_OPEN
		index.stan.F10.open = (i & 1 << 7) != 0;	//7 L_F10_OPEN

		index.stan.klet4a.work = (i & 1 << 8) != 0;	//8 L_R4A_OK клеть работает
		//9 L_O2_OK
		index.stan.F5.work = (i & 1 << 10) != 0;	//10 L_F5_OK
		index.stan.F6.work = (i & 1 << 11) != 0;	//11 L_F6_OK
		index.stan.F7.work = (i & 1 << 12) != 0;	//12 L_F7_OK
		index.stan.F8.work = (i & 1 << 13) != 0;	//13 L_F8_OK
		index.stan.F9.work = (i & 1 << 14) != 0;	//14 L_F9_OK
		index.stan.F10.work = (i & 1 << 15) != 0;	//15 L_F10_OK
//	Word 6
		i = readWord(in);
		//0 L_ROLL_COOL_O2_ON охлаждение клети включено
		index.stan.F5.cool = (i & 1 << 1) != 0;			//1 L_ROLL_COOL_F5_ON
		index.stan.F6.cool = (i & 1 << 2) != 0;			//2 L_ROLL_COOL_F6_ON
		index.stan.F7.cool = (i & 1 << 3) != 0;			//3 L_ROLL_COOL_F7_ON
		index.stan.F8.cool = (i & 1 << 4) != 0;			//4 L_ROLL_COOL_F8_ON
		index.stan.F9.cool = (i & 1 << 5) != 0;			//5 L_ROLL_COOL_F9_ON
		index.stan.F10.cool = (i & 1 << 6) != 0;		//6 L_ROLL_COOL_F10_ON
		//7 L_COOL_O2_F5_ON охлаждение петледержателя включено
		index.stan.loop5_6.cool = (i & 1 << 8) != 0;	//8 L_COOL_F5_F6_ON
		index.stan.loop6_7.cool = (i & 1 << 9) != 0;	//9 L_COOL_F6_F7_ON
		index.stan.loop7_8.cool = (i & 1 << 10) != 0;	//10 L_COOL_F7_F8_ON
		index.stan.loop8_9.cool = (i & 1 << 11) != 0;	//11 L_COOL_F8_F9_ON
		index.stan.loop9_10.cool = (i & 1 << 12) != 0;	//12 L_COOL_F9_F10_ON
//	Word 7
		i = readWord(in);
		//0 L_RED_POW_O2	событие превышение номинальной силы тока
		//1 L_RED_POW_F5
		//2 L_RED_POW_F6
		//3 L_RED_POW_F7
		//4 L_RED_POW_F8
		//5 L_RED_POW_F9
		//6 L_RED_POW_F1
		//7 L_DESCAL_ON				!
//	Word 8
		i = readWord(in);
		//0 L_ORD_MONT_O2_F5		!
		//1 L_ORD_MONT_F5_F6
		//2 L_ORD_MONT_F6_F7
		//3 L_ORD_MONT_F7_F8
		//4 L_ORD_MONT_F8_F9
		//5 L_ORD_MONT_F9_F10
//	Word 9
		i = readWord(in);
		//0 L_DEF_GUID_O2
		index.stan.F5.lineika.work = (i & 1 << 1) == 0;		//1 L_DEF_GUID_F5 линейка не работает
		index.stan.F6.lineika.work = (i & 1 << 2) == 0;		//2 L_DEF_GUID_F6
		index.stan.F7.lineika.work = (i & 1 << 3) == 0;		//3 L_DEF_GUID_F7
		index.stan.F8.lineika.work = (i & 1 << 4) == 0;		//4 L_DEF_GUID_F8
		index.stan.F9.lineika.work = (i & 1 << 5) == 0;		//5 L_DEF_GUID_F9
		index.stan.F10.lineika.work = (i & 1 << 6) == 0;	//6 L_DEF_GUID_F10
//	Word 10
		i = readWord(in);
		//0 L_DEF_SCR_O_O2 винт регулировки раствора клети не работает
		//1 L_DEF_SCR_D_O2
		index.stan.F5.work_scr_o = (i & 1 << 2) == 0;	//2 L_DEF_SCR_O_F5
		index.stan.F5.work_scr_d = (i & 1 << 3) == 0;	//3 L_DEF_SCR_D_F5
		index.stan.F6.work_scr_o = (i & 1 << 4) == 0;	//4 L_DEF_SCR_O_F6
		index.stan.F6.work_scr_d = (i & 1 << 5) == 0;	//5 L_DEF_SCR_D_F6
		index.stan.F7.work_scr_o = (i & 1 << 6) == 0;	//6 L_DEF_SCR_O_F7
		index.stan.F7.work_scr_d = (i & 1 << 7) == 0;	//7 L_DEF_SCR_D_F7
		index.stan.F8.work_scr_o = (i & 1 << 8) == 0;	//8 L_DEF_SCR_O_F8
		index.stan.F8.work_scr_d = (i & 1 << 9) == 0;	//9 L_DEF_SCR_D_F8
		index.stan.F9.work_scr_o = (i & 1 << 10) == 0;	//10 L_DEF_SCR_O_F9
		index.stan.F9.work_scr_d = (i & 1 << 11) == 0;	//11 L_DEF_SCR_D_F9
		index.stan.F10.work_scr_o = (i & 1 << 12) == 0;	//12 L_DEF_SCR_O_F10
		index.stan.F10.work_scr_d = (i & 1 << 13) == 0;	//13 L_DEF_SCR_D_F10
//	Word 11
		i = readWord(in);
		//0 L_DEF_MAX_CUR_O2	// превышено максимальное напряжение
		//1 L_DEF_MAX_CUR_F5
		//2 L_DEF_MAX_CUR_F6
		//3 L_DEF_MAX_CUR_F7
		//4 L_DEF_MAX_CUR_F8
		//5 L_DEF_MAX_CUR_F9
		//6 L_DEF_MAX_CUR_F10
//	Word 12
		i = readWord(in);
		//0 L_DEF_MAX_VIT_O2	// превышена максимальная скорость
		//1 L_DEF_MAX_VIT_F5
		//2 L_DEF_MAX_VIT_F6
		//3 L_DEF_MAX_VIT_F7
		//4 L_DEF_MAX_VIT_F8
		//5 L_DEF_MAX_VIT_F9
		//6 L_DEF_MAX_VIT_F10
//	Word 13
		i = readWord(in);
		//0 L_DEF_MAX_PW_O2		// превышена  максимальная мощность
		//1 L_DEF_MAX_PW_F5
		//2 L_DEF_MAX_PW_F6
		//3 L_DEF_MAX_PW_F7
		//4 L_DEF_MAX_PW_F8
		//5 L_DEF_MAX_PW_F9
		//6 L_DEF_MAX_PW_F10
//	Word 14
		i = readWord(in);
		//0 L_DEF_LOOP_O2_F5 петледержатель не работает
		index.stan.loop5_6.work = (i & 1 << 1) == 0;	//1 L_DEF_LOOP_F5_F6
		index.stan.loop6_7.work = (i & 1 << 2) == 0;	//2 L_DEF_LOOP_F6_F7
		index.stan.loop7_8.work = (i & 1 << 3) == 0;	//3 L_DEF_LOOP_F7_F8
		index.stan.loop8_9.work = (i & 1 << 4) == 0;	//4 L_DEF_LOOP_F8_F9
		index.stan.loop9_10.work = (i & 1 << 5) == 0;	//5 L_DEF_LOOP_F9_F10
//	Word 15
		in.skip(2);
//	Word 16
		i = readWord(in);
		//0 L_BOB_HEAD_POS_1		!
		//1 L_BOB_HEAD_POS_2
		//2 L_BOB_HEAD_POS_3
		index.stan.wind_1.sis = (i & 1 << 3) != 0;	//3 L_BOB_SIS_1 лента мотается на моталке
		index.stan.wind_2.sis = (i & 1 << 4) != 0;	//4 L_BOB_SIS_2
		index.stan.wind_3.sis = (i & 1 << 5) != 0;	//5 L_BOB_SIS_3
		//6 L_AUTO_BOB		!

// DIGITAL SIGNALS FROM AGC
		in.skip(12);
//	Word 1
		//0 L_PR_HEALTHY				!
		//1 L_TH_HEALTHYL_PR_HEALTHY	!
		//2 L_GAUGE_FLAT_DEF			!
		//3 L_UP_STR_REG_SEL			!
		//4 L_REGUL_F8_F9_SEL			!
		//5 L_REGUL_F9_F10_SEL			!
		//6 L_DOWN_STR_REG_SEL			!
		//7 L_FLAT_REG_SEL				!
		//8 L_UP_STR_REG_FROZ			!
		//9 L_REGUL_F8_F9_FROZ			!
		//10 L_REGUL_F9_F10_FROZ		!
		//11 L_DOWN_STR_REG_FROZ		!
		//12 L_FLAT_REG_FROZ			!
//	Word 2
		//0 L_PR_CALIB_DUE			!
		//1 L_PR_CALIB_IN_PROG		!
		//2 L_PR_MES				!
		//3 L_PR_CL					!
		//4 L_PR_PARK				!
		//5 L_PR_XRAY_ON			!
		//6 L_PR_SHUT_OPEN			!
		//7 L_PR_COMP_MODE			!
		//8 L_PR_ZERO				!
		//9 L_PR_STAND_BY			!
		//10 L_PR_PROF				!
		//11 L_PR_SEQ_OFF			!
		//12 L_AGC_PR_STRIP_PRES	!
		//13 L_AGC_PR_AUTO_ZERO		!
		//14 L_AGC_PR_GO_STB		!
		//15 L_AGC_PR_START_PROF	!
//	Word 3
		//0 L_TH_CALIB_DUE			!
		//1 L_TH_CALIB_IN_PROG		!
		//2 L_TH_MES				!
		//3 L_TH_CL					!
		//4 L_TH_PARK				!
		//5 L_TH_XRAY_ON			!
		//6 L_TH_SHUT_OPEN			!
		//7 L_TH_COMP_MODE			!
		//8 L_TH_ZERO				!
		//9 L_AGC_TH_STRIP_PRES		!
		//10 L_AGC_TH_AUTO_ZERO		!
//	Word 4
		//0 L_WID_POS_DEF			!
		//1 L_LASER_OK				!
		//2 L_DRIVE_OK				!
		//3 L_VME_OK				!
		//4 L_SIMUL_IN_PROG			!
		//5 L_CALIB_IN_PROG			!
		//6 L_GAUGE_ON				!
		//7 L_AUTO_MANU				!
		//8 L_CLOSE_SHUT			!
		//9 L_FLAT_VAL				!
//	Word 5
		//0 L_DEF_FOR_R4A			!
		//1 L_DEF_FOR_O2
		//2 L_DEF_FOR_F5
		//3 L_DEF_FOR_F6
		//4 L_DEF_FOR_F7
//	Word 6
		//0 L_SCR_ZERO_R4A		!
		//1 L_SCR_ZERO_O2
		//2 L_SCR_ZERO_F5
		//3 L_SCR_ZERO_F6
		//4 L_SCR_ZERO_F7
		//5 L_SCR_ZERO_F8
		//6 L_SCR_ZERO_F9
		//7 L_SCR_ZERO_F10
		//8 L_SCR_OPEN_F5		!
		//9 L_SCR_OPEN_F6
		//10 L_SCR_OPEN_F7
		//11 L_SCR_OPEN_F8
		//12 L_SCR_OPEN_F9
		//13 L_SCR_OPEN_F10
// DIGITAL SIGNALS FROM HGC
		in.skip(8);
//	Word 1
		//0 L_CIRCUIT_1_OFF_F8		!
		//1 L_CIRCUIT_2_OFF_F8		!
		//2 L_CORR_BEND_F8			!
		//3 L_DEF_BEND_F8			!
//	Word 2
		//0 L_CIRCUIT_1_OFF_F9
		//1 L_CIRCUIT_2_OFF_F9
		//2 L_CORR_BEND_F9
		//3 L_DEF_BEND_F9
//	Word 3
		//0 L_CIRCUIT_1_OFF_F10
		//1 L_CIRCUIT_2_OFF_F10
		//2 L_CORR_BEND_F10
		//3 L_DEF_BEND_F10
//	Word 4
		//1 L_BP_PERTU1			!
		//2 L_ESS_LAMP_OD2		!
		//3 L_ARRET_ANORMAL1	!
		//4 L_ROLL_CHANGE		!
		//5 L_ZER_IN_PGRS_F8	!
		//6 L_ZER_IN_PGRS_F9
		//7 L_ZER_IN_PGRS_F10
		//8 L_RED_ALARM_HGC		!
		//9 L_RESET_PERTU		!
		//10 L_MODE_ROLL_ZERO	!
		//11 L_MODE_ROLL_PLUS	!
		//12 L_SEM_NORM			!
		//13 L_SEM_ZAKAZ		!
//	Word 5
		i = readWord(in);
		index.stan.temp = (short) i;// !!!!!
		//0 L_COMP_EXC_F8_ACT 		!
		//1 L_COMP_EXC_F9_ACT
		//2 L_COMP_EXC_F10_ACT
		//3 L_COMP_STRE_F8_ACT		!
		//4 L_COMP_STRE_F9_ACT
		//5 L_COMP_STRE_F10_ACT
		//6 L_STR_F8_FREEZE			!
		//7 L_STR_F9_FREEZE
		//8 L_STR_F10_FREEZE
		index.stan.start = (i & 1 << 9) != 0;		//9 L_TRANZIT_FR	сляб перед черновой группой
		index.stan.tranzit = (i & 1 << 10) != 0;	//10 L_TRANZIT_SW	режим транзит
		index.stan.pech1 = (i & 1 << 11) != 0;		//11 L_FURNACE_3	сляб выпал с печи 1
		index.stan.pech2 = (i & 1 << 12) != 0;		//12 L_FURNACE_4	сляб выпал с печи 2
		index.stan.pech3 = (i & 1 << 13) != 0;		//13 L_FURNACE_5	сляб выпал с печи 3
		//14 L_SHIRM				!
//
		in.skip(58);
	}

	// 252  32 gjdth
	private void readAnaMas(InputStream in) throws IOException
	{
		in.skip(2);									//CURRENT_O2	ток, А
		index.stan.F5.motor.I = readShort(in);		//CURRENT_F5
		index.stan.F6.motor.I = readShort(in);		//CURRENT_F6
		index.stan.F7.motor.I = readShort(in);		//CURRENT_F7
		index.stan.F8.motor.I = readShort(in);		//CURRENT_F8
		index.stan.F9.motor.I = readShort(in);		//CURRENT_F9
		index.stan.F10.motor.I = readShort(in);		//CURRENT_F10

		in.skip(2);									//VOLT_O2		напряжение, V
		index.stan.F5.motor.V = readShort(in);		//VOLT_F5
		index.stan.F6.motor.V = readShort(in);		//VOLT_F6
		index.stan.F7.motor.V = readShort(in);		//VOLT_F7
		index.stan.F8.motor.V = readShort(in);		//VOLT_F8
		index.stan.F9.motor.V = readShort(in);		//VOLT_F9
		index.stan.F10.motor.V = readShort(in);		//VOLT_F10

		in.skip(2);									//POS_LOOP_O2_F5	положение петледержателя, mm
		index.stan.loop5_6.pos = readShort(in);		//POS_LOOP_F5_F6
		index.stan.loop6_7.pos = readShort(in);		//POS_LOOP_F6_F7
		index.stan.loop7_8.pos = readShort(in);		//POS_LOOP_F7_F8
		index.stan.loop8_9.pos = readShort(in);		//POS_LOOP_F8_F9
		index.stan.loop9_10.pos = readShort(in);	//POS_LOOP_F9_F10

		in.skip(2);										//REF_SPEED_O2		заданная скорость двигателя, оборотов в минуту
		index.stan.F5.motor.ref_speed = (short) (readShort(in) * index.stan.F5.motor.reductor);	//REF_SPEED_F5
		index.stan.F6.motor.ref_speed = (short) (readShort(in) * index.stan.F6.motor.reductor);	//REF_SPEED_F6
		index.stan.F7.motor.ref_speed = readShort(in);	//REF_SPEED_F7
		index.stan.F8.motor.ref_speed = readShort(in);	//REF_SPEED_F8
		index.stan.F9.motor.ref_speed = readShort(in);	//REF_SPEED_F9
		index.stan.F10.motor.ref_speed = readShort(in);	//REF_SPEED_F10

		in.skip(2);										//SPEED_O2		текущяя скорость двигателя, оборотов в минуту
		index.stan.F5.motor.speed = (short) (readShort(in) * index.stan.F5.motor.reductor);		//SPEED_F5
		index.stan.F6.motor.speed = (short) (readShort(in) * index.stan.F6.motor.reductor);		//SPEED_F6
		index.stan.F7.motor.speed = readShort(in);		//SPEED_F7
		index.stan.F8.motor.speed = readShort(in);		//SPEED_F8
		index.stan.F9.motor.speed = readShort(in);		//SPEED_F9
		index.stan.F10.motor.speed = readShort(in);		//SPEED_F10

		index.stan.t1.setT(readShort(in));			//PYRO1_TEMP	показания пирометра, °C
		index.stan.t2.setT(readShort(in));			//PYRO2_TEMP
		index.stan.t3.setT(readShort(in));			//PYRO3_TEMP
		index.stan.t4.setT(readShort(in));			//PYRO4_TEMP
		index.stan.t5.setT(readShort(in));			//PYRO5_TEMP

		//in.skip(252-39*2=174);
		in.skip(134);
		/*
		//WID_R4A                  // ! [mm] нет
		//WID_F10                  // ! [mm]

		//FILL[19]

		//ADA_GLISS_O2             // ![1] parameter for adaptation of O2  sliding from MASTER
		//ADA_GLISS_F5             // [1] parameter for adaptation of F5  sliding from MASTER
		//ADA_GLISS_F6             // [1] parameter for adaptation of F6  sliding from MASTER
		//ADA_GLISS_F7             // [1] parameter for adaptation of F7  sliding from MASTER
		//ADA_GLISS_F8             // [1] parameter for adaptation of F8  sliding from MASTER
		//ADA_GLISS_F9             // [1] parameter for adaptation of F9  sliding from MASTER
		//ADA_GLISS_F10            // [1] parameter for adaptation of F10 sliding from MASTER
		//DULAMS[7]                // ! roll_time*10
//  These 52 reserw words are declared now (26.12.96) as reserw(7,7)+%FILL(3)
      ,               RESERV[32]              // 7 different params(on 7stands)
      */
		index.stan.klet0.motor.P = readShort(in);	// мощность клети
		index.stan.klet1.motor.P = readShort(in);	// мощность клети
		index.stan.klet2.motor.P = readShort(in);	// мощность клети
		index.stan.klet3.motor.P = readShort(in);	// мощность клети
		index.stan.klet4.motor.P = readShort(in);	// мощность клети
		index.stan.klet4a.motor.P = readShort(in);	// мощность клети
		index.stan.t6.setT(readShort(in));			// температура перед моталками
		in.skip(26);
		/*
						reserv[11]
	  ,               FILL1 [3];

      */
	}

	// 160
	private void readAnaAgc(InputStream in) throws IOException
	{
		index.stan.klet4a.op_force = readShort(in);		// !!! 0 ! OP_FORCE_R4A		усилие на валок на стороне оператора, тон
		in.skip(2);										//OP_FORCE_O2
		index.stan.F5.op_force = readShort(in);			//OP_FORCE_F5
		index.stan.F6.op_force = readShort(in);			//OP_FORCE_F6
		index.stan.F7.op_force = readShort(in);			//OP_FORCE_F7
		index.stan.klet4a.dr_force = readShort(in);		// !!! 0 ! DR_FORCE_R4A		усилие на валок на стороне маш зала, тон
		in.skip(2);										//DR_FORCE_02
		index.stan.F5.dr_force = readShort(in);			//DR_FORCE_F5
		index.stan.F6.dr_force = readShort(in);			//DR_FORCE_F6
		index.stan.F7.dr_force = readShort(in);			//DR_FORCE_F7

		index.stan.thickness.h1 = readShort(in);			// TH_DEV1	[-10%(-2^16+1) 10%(2^16)]	последние четыре значения изменения толщины полосы от номинальой
		index.stan.thickness.h2 = readShort(in);			// TH_DEV2	[-10%(-2^16+1) 10%(2^16)]
		index.stan.thickness.h3 = readShort(in);			// TH_DEV3	[-10%(-2^16+1) 10%(2^16)]
		index.stan.thickness.h4 = readShort(in);			// TH_DEV4	[-10%(-2^16+1) 10%(2^16)]
		index.stan.thickness.setH();

		int i;
		i = readShort(in);								//TH_DEV	! summary dev
		i = readShort(in);								//PR_DEV	! // +/- 10% profile gauge deviation         // digital signals from profile gauge
		i = readShort(in);								//COR_DEV	! // correlated deviation
		i = readShort(in);								//FF_SPEED	! // ?????????????????
		i = readShort(in);								//PR_POS	// [+/- 1000 mm] положение профилемера относительно центра полосы

		i = readShort(in);								//ELONG_OP	! удлинение полосы на стороне оператора
		i = readShort(in);								//ELONG_CL	! удлинение полосы по средине
		i = readShort(in);								//ELONG_DR	! удлинение полосы на стороме машзала
		i = readShort(in);								//DISSYM	! дисеметрия
		i = readShort(in);								//FLATNESS	! удлинение среднее

		index.stan.klet4a.op_pos = readShort(in);			//OP_SCR_POS_R4A	положение винта со стороны оператора, [10*micron]
		in.skip(2);										//OP_SCR_POS_O2
		index.stan.F5.op_pos = readShort(in);			//OP_SCR_POS_F5
		index.stan.F6.op_pos = readShort(in);			//OP_SCR_POS_F6
		index.stan.F7.op_pos = readShort(in);			//OP_SCR_POS_F7
		index.stan.F8.op_pos = readShort(in);			//OP_SCR_POS_F8
		index.stan.F9.op_pos = readShort(in);			//OP_SCR_POS_F9
		index.stan.F10.op_pos = readShort(in);			//OP_SCR_POS_F10

		in.skip(2);										//DR_SCR_POS_O2		положение винта со стороны машзала, [10*micron]
		index.stan.F5.dr_pos = readShort(in);			//DR_SCR_POS_F5
		index.stan.F6.dr_pos = readShort(in);			//DR_SCR_POS_F6
		index.stan.F7.dr_pos = readShort(in);			//DR_SCR_POS_F7
		index.stan.F8.dr_pos = readShort(in);			//DR_SCR_POS_F8
		index.stan.F9.dr_pos = readShort(in);			//DR_SCR_POS_F9
		index.stan.F10.dr_pos = readShort(in);			//DR_SCR_POS_F10

		in.skip(28);									//FILL[14]

		index.stan.klet4a.zer_force = readShort(in);	//ZER_FORCE_R4A		усилия обнуления, тон
		in.skip(2);										//ZER_FORCE_O2
		index.stan.F5.zer_force = readShort(in);		//ZER_FORCE_F5
		index.stan.F6.zer_force = readShort(in);		//ZER_FORCE_F6
		index.stan.F7.zer_force = readShort(in);		//ZER_FORCE_F7

		in.skip(24);									//FILL1[12]

		in.skip(20);
		//LIM_FORCE[3]		[ton] 3 force limits oper input
		//FILL2[7]
	}

	// 248
	private void readAnaHgc(InputStream in) throws IOException
	{
		int i;
		index.stan.F8.dr_force = readShort(in);			//DR_FORCE_F8			[ton]
		index.stan.F8.op_force = readShort(in);			//OP_FORCE_F8			[ton]
		index.stan.F8.pos_cyl_dr = readShort(in);		//DR_CYL_POS_F8			[micron]
		index.stan.F8.pos_cyl_op = readShort(in);		//OP_CYL_POS_F8			[micron]
		index.stan.F8.pos_cyl_ref = readShort(in);		//REF_CYL_POS_F8		[micron]
		index.stan.F8.zer_force = readShort(in);		//ZER_FORCE_F8			[ton] mean zeroing force DR + OP
		in.skip(2);										//TOT_FORCE_F8			[ton] DR + OP
		i = readShort(in);								//DIF_CYL_POS_F8		[micron] разность позиции цилиндров operator - drive
		i = readShort(in);								//GAP_CYL_POS_F8		[micron] значение раствора
		i = readShort(in);								//MESS_NUM_F8			[1] zeroing state (N -  0: ZER_FORCE_F8 valid, end of zeroing)

		index.stan.F9.dr_force = readShort(in);			//DR_FORCE_F9			[ton]
		index.stan.F9.op_force = readShort(in);			//OP_FORCE_F9			[ton]
		index.stan.F9.pos_cyl_dr = readShort(in);		//DR_CYL_POS_F9			[micron]
		index.stan.F9.pos_cyl_op = readShort(in);		//OP_CYL_POS_F9			[micron]
		index.stan.F9.pos_cyl_ref = readShort(in);		//REF_CYL_POS_F9		[micron]
		index.stan.F9.zer_force = readShort(in);		//ZER_FORCE_F9			[ton] mean zeroing force DR + OP
		in.skip(2);										//TOT_FORCE_F9			[ton] DR + OP
		i = readShort(in);								//DIF_CYL_POS_F9		[micron] convention drive - operator
		i = readShort(in);								//GAP_CYL_POS_F9		[micron] roll gap
		i = readShort(in);								//MESS_NUM_F9			[1] zeroing state (N -  0: ZER_FORCE_F9 valid, end of zeroing)

		index.stan.F10.dr_force = readShort(in);		//DR_FORCE_F10			[ton]
		index.stan.F10.op_force = readShort(in);		//OP_FORCE_F10			[ton]
		index.stan.F10.pos_cyl_dr = readShort(in);		//DR_CYL_POS_F10		[micron]
		index.stan.F10.pos_cyl_op = readShort(in);		//OP_CYL_POS_F10		[micron]
		index.stan.F10.pos_cyl_ref = readShort(in);		//REF_CYL_POS_F10		[micron]
		index.stan.F10.zer_force = readShort(in);		//ZER_FORCE_F10			[ton] mean zeroing force DR + OP
		in.skip(2);										//TOT_FORCE_F10			[ton] DR + OP
		i = readShort(in);								//DIF_CYL_POS_F10		[micron] convention drive - operator
		i = readShort(in);								//GAP_CYL_POS_F10		[micron] roll gap
		i = readShort(in);								//MESS_NUM_F10			[1] zeroing state (N -  0: ZER_FORCE_F10 valid, end of zeroing)

		i = readShort(in);								//BEND1_F8				[0.1*bar] bending pressure circuit 1 near cylinders   stand F8 PT801
		i = readShort(in);								//BEND2_F8				[0.1*bar] bending pressure circuit 1 near servovalves stand F8 PT804
		i = readShort(in);								//BEND3_F8				[0.1*bar] bending pressure circuit 1 near cylinders   stand F8 PT802
		i = readShort(in);								//BEND4_F8				[0.1*bar] bending pressure circuit 1 near servovalves stand F8 PT805
		i = readShort(in);								//MAX_BEND_F8			[0.1*t]   bending force stand F8
		i = readShort(in);								//BEND_REFER_TOT_F8		[0.1 T]   bending reference (operator+regul)
		i = readShort(in);								//BEND_PRS_LVL8
		in.skip(2);										//FILL[1]

		i = readShort(in);								//BEND1_F9				[0.1*bar] bending pressure circuit 1 near cylinders   stand F9 PT901
		i = readShort(in);								//BEND2_F9				[0.1*bar] bending pressure circuit 1 near servovalves stand F9 PT904
		i = readShort(in);								//BEND3_F9				[0.1*bar] bending pressure circuit 1 near cylinders   stand F9 PT902
		i = readShort(in);								//BEND4_F9				[0.1*bar] bending pressure circuit 1 near servovalves stand F5 PT905
		i = readShort(in);								//MAX_BEND_F9			[0.1*t]   bending force stand F9
		i = readShort(in);								//BEND_REFER_TOT_F9		[0.1 T]   bending reference (operator+regul)
		i = readShort(in);								//BEND_PRS_LVL9
		in.skip(2);										//FILL1[1]

		i = readShort(in);								//BEND1_F10				[0.1*bar] bending pressure circuit 1 near cylinders   stand F10 PT1001
		i = readShort(in);								//BEND2_F10				[0.1*bar] bending pressure circuit 1 near servovalves stand F10 PT1004
		i = readShort(in);								//BEND3_F10				[0.1*bar] bending pressure circuit 1 near cylinders   stand F10 PT1002
		i = readShort(in);								//BEND4_F10				[0.1*bar] bending pressure circuit 1 near servovalves stand F10 PT1005
		i = readShort(in);								//MAX_BEND_F10			[0.1*t]   bending force stand F10
		i = readShort(in);								//BEND_REFER_TOT_F10	[0.1 T]   bending reference (operator+regul)
		i = readShort(in);								//BEND_PRS_LVL10
		in.skip(2);										//FILL3[1]

		i = readShort(in);								//PRES_LOOP_O2_F5_BOT	[bar]
		i = readShort(in);								//PRES_LOOP_O2_F5_UP	[bar]
		i = readShort(in);								//TRAC_O2_F5			[ton*0.1]
		in.skip(2);										//FILL4[1]

		i = readShort(in);								//PRES_LOOP_F5_F6_BOT	[bar]
		i = readShort(in);								//PRES_LOOP_F5_F6_UP	[bar]
		i = readShort(in);								//TRAC_F5_F6			[ton*0.1]
		in.skip(2);										//FILL5[1]

		i = readShort(in);								//PRES_LOOP_F6_F7_BOT	[bar]
		i = readShort(in);								//PRES_LOOP_F6_F7_UP	[bar]
		i = readShort(in);								//TRAC_F6_F7			[ton*0.1]
		in.skip(2);										//FILL6[1]

		i = readShort(in);								//PRES_LOOP_F7_F8_BOT	[bar]
		i = readShort(in);								//PRES_LOOP_F7_F8_UP	[bar]
		i = readShort(in);								//TRAC_F7_F8			[ton*0.1]
		in.skip(2);										//FILL7[1]

		i = readShort(in);								//PRES_LOOP_F8_F9_BOT	[bar]
		i = readShort(in);								//PRES_LOOP_F8_F9_UP	[bar]
		i = readShort(in);								//TRAC_F8_F9			[ton*0.1]
		in.skip(2);										//FILL8[1]

		i = readShort(in);								//PRES_LOOP_F9_F10_BOT	[bar]
		i = readShort(in);								//PRES_LOOP_F9_F10_UP	[bar]
		i = readShort(in);								//TRAC_F9_F10			[ton*0.1]
		in.skip(2);										//FILL9[1]


		in.skip(2);										//FILL10[1]
		i = readShort(in);								//MEA_EXCENT_F8			[micron] Measured excentricity stand F8
		i = readShort(in);								//MEA_EXCENT_F9			[micron] Measured excentricity stand F9
		i = readShort(in);								//MEA_EXCENT_F10		[micron] Measured excentricity stand F10

		i = readShort(in);								//DV_VDT_F7				[2^15 10%] Correction for drive F7
		i = readShort(in);								//STR_CORR_F8			[micron] Stretch correction F8
		i = readShort(in);								//AGC_CORR_F8			[micron] Feed back correction F8
		i = readShort(in);								//GAP_REF_F8			[micron] Gap reference F8
		i = readShort(in);								//STR_COMP_F8			[micron] Stretch compensation F8
		i = readShort(in);								//OILFILM_COR_F8		[micron] Oil film correction F8
		i = readShort(in);								//DH_DT_AGC_F8			[2^15 10%] Speed correction done F8
		i = readShort(in);								//DH_AGC_F8				[micron] Total correction done F8
		i = readShort(in);								//GAP_MES_F8			[??????] Measured Gap F8
		in.skip(10);									//FILL11[5]

		i = readShort(in);								//DV_VDT_F8				[2^15 10%]	Correction for drive F8
		i = readShort(in);								//STR_CORR_F9			[micron]	Stretch correction F9
		i = readShort(in);								//AGC_CORR_F9			[micron]	Feed back correction F9
		i = readShort(in);								//GAP_REF_F9			[micron]	Gap reference F9
		i = readShort(in);								//STR_COMP_F9			[micron]	Streth compensation F9
		i = readShort(in);								//OILFILM_COR_F9		[micron]	Oil film correction F9
		i = readShort(in);								//DH_DT_AGC_F9			[2^15 10%]	Speed correction done F9
		i = readShort(in);								//DH_AGC_F9				[micron]	Total correction done F9
		i = readShort(in);								//GAP_MES_F9			[??????]	Measured Gap F9
		in.skip(10);									//FILL12[5]

		i = readShort(in);								//DV_VDT_F9				[2^15 10%]	Correction for drive F9
		i = readShort(in);								//STR_CORR_F1			[micron]	Stretch correction F10
		i = readShort(in);								//AGC_CORR_F10			[micron]	Feed back correction F10
		i = readShort(in);								//GAP_REF_F10			[micron]	Gap reference F10
		i = readShort(in);								//STR_COMP_F10			[micron]	Stretch compensation F10
		i = readShort(in);								//OILFILM_COR_F10		[micron]	Oil film correction F10
		i = readShort(in);								//DH_DT_AGC_F10			[2^15 10%]	Speed correction done F10
		i = readShort(in);								//DH_AGC_F10			[micron]	Total correction done F10
		i = readShort(in);								//GAP_MES_F10			[??????]	Measured Gap F10
		in.skip(10);									//FILL13[5]
	}

	// 20
	private void readAnaPlc1(InputStream in) throws IOException
	{
		in.skip(20);
		/*
		* short int PLC1_ANA_RES[10]
		* */

	}

	// 200
	private void readAnaPlc2(InputStream in) throws IOException
	{
		in.skip(200);
		/*
		* short int
		   E_POS_SUP_E_F8       // Measured Pos.cyl.Top entry f8
E_POS_INF_E_F8       // Measured Pos.cyl.Bot entry f8
E_POS_SUP_S_F8       // Measured Pos.cyl.Top exit  f8
E_POS_INF_S_F8       // Measured Pos.cyl.Bot exit  f8
E_PRS_F_SUP_F8       // Measured Press.cyl.Top entry f8
E_PRS_F_INF_F8       // Measured Press.cyl.BOT entry f8
E_PRS_T_SUP_F8       // Measured Press.cyl.Top exit  f8
E_PRS_T_INF_F8       // Measured Press.cyl.Bot exit  f8

E_POS_SUP_E_F9       // Measured Pos.cyl.Top entry f9
E_POS_INF_E_F9       // Measured Pos.cyl.Bot entry f9
E_POS_SUP_S_F9       // Measured Pos.cyl.Top exit  f9
E_POS_INF_S_F9       // Measured Pos.cyl.Bot exit  f9
E_PRS_F_SUP_F9       // Measured Press.cyl.Top entry f9
E_PRS_F_INF_F9       // Measured Press.cyl.BOT entry f9
E_PRS_T_SUP_F9       // Measured Press.cyl.Top exit  f9
E_PRS_T_INF_F9       // Measured Press.cyl.Bot exit  f9

E_POS_SUP_E_F10       // Measured Pos.cyl.Top entry f10
E_POS_INF_E_F10       // Measured Pos.cyl.Bot entry f10
E_POS_SUP_S_F10       // Measured Pos.cyl.Top exit  f10
E_POS_INF_S_F10       // Measured Pos.cyl.Bot exit  f10
E_PRS_F_SUP_F10       // Measured Press.cyl.Top entry f10
E_PRS_F_INF_F10       // Measured Press.cyl.BOT entry f10
E_PRS_T_SUP_F10       // Measured Press.cyl.Top exit  f10
E_PRS_T_INF_F10       // Measured Press.cyl.Bot exit  f10

      ,            EQUI_F8               // Measured press. of balance f8
      ,            EQUI_F9               // Measured press. of balance f9
      ,            EQUI_F10              // Measured press. of balance f10
      ,            FILL[5]              // 5 spare words
      ,            DEM_ARRET_AUT2        // Request to stop the mill     (1 bit)
      ,            DCF8_FLAG             // Shifting modes (logical) :
//1               Mode Manual
//2               Mode Operator
//3               Mode Calculator
//4               Mode Cycle
//5               State Manual
//6               Entry Top sensor  OK
//7               Entry Bot sensor  OK
//8               Exit  Top sensor  OK
//9               Exit  Bot sensor  OK
//10              Shifting in progress
//11              Request Forsage
//12              Request for shifting
//13              Request to open cage for shifting (really - request to shift)
//14              Shifting'd finished
//15              Request to stop the mill after strips end because of shift.problem
//16              Request to stop the mill quickly because of shift.problem

      ,          ROLL_CH_F8              // Roll change in progress f8
      ,          FILL1[1]
      ,          S_POS_SUP_F8            // Average measured pos Top
      ,          S_POS_INF_F8            // Average measured pos Bot
      ,          S_FOR_SUP_F8            // Force of shifting Top
      ,          S_FOR_INF_F8            // Force of shifting Bot
      ,          S_REF_MEMO_F8           // memorized reference
      ,          S_REF_NEXT_F8           // Next  reference
      ,          FILL2[4]
      ,          REF_CYKL_F8             // Reference Mode Cykle
      ,          REF_OPE_F8              // Reference Mode Operator
      ,          REF_CALC_F8             // Reference Mode Calculator
      ,          SPEED_CAGE_F8           // speed f8
      ,          PRESS_D6                //  descales before f5
      ,          PRESS_D7                //
      ,          FILL3[1]

      ,            DCF9_FLAG             // Shifting modes (logical) :
//1               Mode Manual
//2               Mode Operator
//3               Mode Calculator
//4               Mode Cycle
//5               State Manual
//6               Entry Top sensor  OK
//7               Entry Bot sensor  OK
//8               Exit  Top sensor  OK
//9               Exit  Bot sensor  OK
//10              Shifting in progress
//11              Request Forsage
//12              Request for shifting
//13              Request to open cage for shifting (really - request to shift)
//14              Shifting'd finished
//15              Request to stop the mill after strips end because of shift.problem
//16              Request to stop the mill quickly because of shift.problem

      ,          ROLL_CH_F9              // Roll change in progress
      ,          FILL4[1]
      ,          S_POS_SUP_F9            // Average measured pos Top
      ,          S_POS_INF_F9            // Average measured pos Bot
      ,          S_FOR_SUP_F9            // Force of shifting Top
      ,          S_FOR_INF_F9            // Force of shifting Bot
      ,          S_REF_MEMO_F9           // memorized reference
      ,          S_REF_NEXT_F9           // Next  reference
      ,          FILL5[4]
      ,          REF_CYKL_F9             // Reference Mode Cykle
      ,          REF_OPE_F9              // Reference Mode Operator
      ,          REF_CALC_F9             // Reference Mode Calculator
      ,          SPEED_CAGE_F9           // speed f8
      ,          FILL6[3]


      ,            DCF10_FLAG             // Shifting modes (logical) :
//1               Mode Manual
//2               Mode Operator
//3               Mode Calculator
//4               Mode Cycle
//5               State Manual
//6               Entry Top sensor  OK
//7               Entry Bot sensor  OK
//8               Exit  Top sensor  OK
//9               Exit  Bot sensor  OK
//10              Shifting in progress
//11              Request Forsage
//12              Request for shifting
//13              Request to open cage for shifting (really - request to shift)
//14              Shifting'd finished
//15              Request to stop the mill after strips end because of shift.problem
//16              Request to stop the mill quickly because of shift.problem
      ,          ROLL_CH_F10              // Roll change in progress f10
      ,          FILL7[1]
      ,          S_POS_SUP_F10            // Average measured pos Top
      ,          S_POS_INF_F10            // Average measured pos Bot
      ,          S_FOR_SUP_F10            // Force of shifting Top
      ,          S_FOR_INF_F10            // Force of shifting Bot
      ,          S_REF_MEMO_F10           // memorized reference
      ,          S_REF_NEXT_F10           // Next  reference
      ,          FILL8[4]
      ,          REF_CYKL_F10             // Reference Mode Cykle
      ,          REF_OPE_F10              // Reference Mode Operator
      ,          REF_CALC_F10             // Reference Mode Calculator
      ,          SPEED_CAGE_F10           // speed f10
      ,          FILL9[10]
	*/
	}

	// 128
	private void readAnaGaug(InputStream in) throws IOException
	{
		in.skip(128);
		/*
		*
		* short int       COIL_ID_1
      ,               COIL_ID_2
      ,               COIL_ID_3
      ,               COIL_ID_4
      ,               COIL_ID_5
      ,               COIL_ID_6
      ,               OUTER_CROWN			[mm]
      ,               OUTER_WEDGE			[mm]
      ,               NEXT_CROWN_1			[mm]
      ,               NEXT_WEDGE_1			[mm]
      ,               NEXT_CROWN_2			[mm]
      ,               NEXT_WEDGE_2			[mm]
      ,               INNER_CROWN			[mm]
      ,               INNER_WEDGE			[mm]
      ,               THICK_PROF[40]		[mm]	40 equidistant points
      ,               GAU_RES[10];
		* */
	}

	// 180
	private void readAnaMariou(InputStream in) throws IOException
	{
		in.skip(32);
		/*
		 short int       FILL[2]                 // 2 logical words from Mariupol
      ,               SCR_POS_R4A			[mm*100]
      ,               OP_SCR_POS_O2			[mm*100]
      ,               DR_SCR_POS_O			[mm*100]
      ,               OP_SCR_POS_F5			[mm*100]
      ,               DR_SCR_POS_F5			[mm*100]
      ,               OP_SCR_POS_F6			[mm*100]
      ,               DR_SCR_POS_F6			[mm*100]
      ,               OP_SCR_POS_F7			[mm*100]
      ,               DR_SCR_POS_F7			[mm*100]
      ,               OP_SCR_POS_F8			[mm*100]
      ,               DR_SCR_POS_F8			[mm*100]
      ,               OP_SCR_POS_F9			[mm*100]
      ,               DR_SCR_POS_F9			[mm*100]
      ,               OP_SCR_POS_F10		[mm*100]
      ,               DR_SCR_POS_F10		[mm*100]
      */
		in.skip(2);									// MES_GUI_CIS	[mm]	guide width measure before shears
		in.skip(2);									// MES_GUI_O2	[mm]	guide width measure shears - O2
		index.stan.F5.lineika.wdith = readShort(in);	// MES_GUI_F5	[mm]	ширина линейки
		index.stan.F6.lineika.wdith = readShort(in);	// MES_GUI_F6	[mm]
		index.stan.F7.lineika.wdith = readShort(in);	// MES_GUI_F7	[mm]
		index.stan.F8.lineika.wdith = readShort(in);	// MES_GUI_F8	[mm]
		index.stan.F9.lineika.wdith = readShort(in);	// MES_GUI_F9	[mm]
		index.stan.F10.lineika.wdith = readShort(in);	// MES_GUI_F10	[mm]
//////////////////////////////////////////////////////   very temporary  //////////////
//////////////////old       ,               %FILL(67)
		// 84
		in.skip(82);								//MY_PROF[42]	simulated 40 equidist points
		in.skip(50);								//FILL1[25]
	}

	// 18
	private void readAlarm(InputStream in) throws IOException
	{
		in.skip(18);
	}


	//
	public final short readShort(InputStream in) throws IOException
	{
		int ch1 = in.read();
		int ch2 = in.read();
		if ((ch1 | ch2) < 0)
			throw new EOFException();
		return (short) (ch1 + (ch2 << 8));
	}

	public final int readWord(InputStream in) throws IOException
	{
		int ch1 = in.read();
		int ch2 = in.read();
		if ((ch1 | ch2) < 0)
			throw new EOFException();
		return ch1 + (ch2 << 8);
	}
}
