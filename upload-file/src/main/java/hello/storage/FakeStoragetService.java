package hello.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FakeStoragetService implements StorageService {
    private List<File> files;
    private ResourceLoader resourceLoader;

    @Autowired
    public FakeStoragetService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        files = new ArrayList<>();
    }

    @Override
    public void init() {
        files = new ArrayList<>();
    }

    @Override
    public void store(MultipartFile multipartFile) {
        String tmpdir = System.getProperty("java.io.tmpdir");
        File file = new File(tmpdir + multipartFile.getOriginalFilename());
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        files.add(file);
    }

    @Override
    public Stream<Path> loadAll() {
        return files.stream().map(f -> f.toPath());
    }

    @Override
    public Path load(String filename) {
        return loadAll().filter(p -> p.endsWith(filename)).findFirst().get();
    }

    @Override
    public Resource loadAsResource(String filename) {
        return resourceLoader.getResource(load(filename).toUri().toString());
    }

    @Override
    public void deleteAll() {
        files.clear();
    }
}
