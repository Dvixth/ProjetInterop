import cv2
import numpy as np

def create_filter(size):
    filter = np.zeros((size, size))

    k = 200
    sum_val = 0
    for i in range(size):
        for j in range(size):
            x = i * np.sqrt(2) / ((size - 1) / 2) - np.sqrt(2)
            y = j * np.sqrt(2) / ((size - 1) / 2) - np.sqrt(2)
            filter[i][j] = (k * np.exp(-(x * x + y * y)))
            sum_val += filter[i][j]

    filter /= sum_val
    return filter

def convolution(image, filter):
    height, width = image.shape
    filter_height, filter_width = filter.shape
    pad_height = filter_height // 2
    pad_width = filter_width // 2

    padded_image = cv2.copyMakeBorder(image, pad_height, pad_height, pad_width, pad_width, cv2.BORDER_CONSTANT)
    result_image = np.zeros((height, width), dtype=np.float32)

    for x in range(width):
        for y in range(height):
            conv = 0
            for i in range(filter_height):
                for j in range(filter_width):
                    conv += filter[i][j] * padded_image[y + i][x + j]
            result_image[y][x] = conv
    return result_image

def marr_hildreth(image_path):
    image = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)

    filter_A = create_filter(5)
    filter_B = create_filter(7)

    image_A = convolution(image, filter_A)
    image_B = convolution(image, filter_B)

    result_image = np.abs(image_B - image_A)

    cv2.imwrite(r"./ResultatsImage/marrHildrethpython.png", result_image)

# Appeler la fonction marr_hildreth avec l'image en argument
# Utilisation d'une chaîne brute
marr_hildreth(r"./MyJavaProject/building.png")
