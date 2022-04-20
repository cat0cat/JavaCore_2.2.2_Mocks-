import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

class MedicalServiceImplTest {
    PatientInfo patientInfo;
    PatientInfo info;
    PatientInfoRepository patientInfoRepository;
    String message;
    SendAlertService alertService;
    MedicalService medicalService;

    @BeforeEach
    void setUp() {
        patientInfo = new PatientInfo("Иван", "Петров", LocalDate.of(1980, 11, 26),
                new HealthInfo(new BigDecimal("36.65"), new BloodPressure(120, 80)));
        info = new PatientInfo(UUID.randomUUID().toString(),
                patientInfo.getName(),
                patientInfo.getSurname(),
                patientInfo.getBirthday(),
                patientInfo.getHealthInfo());
        message = String.format("Warning, patient with id: %s, need help", info.getId());
        patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoRepository.getById(info.getId())).thenReturn(info);
        alertService = Mockito.mock(SendAlertService.class);
        Mockito.doNothing().when(alertService).send(message);
        medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
    }

    @Test
    void checkBloodPressureNotNormal() {
        BloodPressure currentPressure = new BloodPressure(130,90);
        medicalService.checkBloodPressure(info.getId(), currentPressure);
        Mockito.verify(alertService, Mockito.times(1)).send(message);
    }

    @Test
    void checkBloodPressureNormal() {
        BloodPressure currentPressure = new BloodPressure(120, 80);
        medicalService.checkBloodPressure(info.getId(), currentPressure);
        Mockito.verify(alertService, Mockito.times(0)).send(message);
    }

    @Test
    void checkTemperatureNotNormal() {
        BigDecimal currentTemperature = new BigDecimal("34.7");
        medicalService.checkTemperature(info.getId(), currentTemperature);
        Mockito.verify(alertService, Mockito.times(1)).send(message);
    }

    @Test
    void checkTemperatureNormal() {
        BigDecimal currentTemperature = new BigDecimal("36.6");
        medicalService.checkTemperature(info.getId(), currentTemperature);
        Mockito.verify(alertService, Mockito.times(0)).send(message);
    }
}
